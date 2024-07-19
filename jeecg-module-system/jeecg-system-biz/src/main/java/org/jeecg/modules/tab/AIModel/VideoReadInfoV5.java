package org.jeecg.modules.tab.AIModel;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.message.websocket.WebSocket;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @date 2024/3/25 9:23
 */

@Slf4j
public class VideoReadInfoV5 implements Runnable{

    private RedisTemplate redisTemplate;
    public String videoUrl;

    public String userId;
    public String namesUrl;
    public String cfgUrl;
    public String weightUrl;
    public WebSocket webSocket;
    public VideoReadInfoV5(String videoUrl, RedisTemplate redisTemplate, String userId, String namesUrl, String cfgUrl, String weightUrl, WebSocket webSocket){
        this.videoUrl=videoUrl;
        this.redisTemplate=redisTemplate;
        this.userId=userId;
        this.namesUrl=namesUrl;
        this.cfgUrl=cfgUrl;
        this.weightUrl=weightUrl;
        this.webSocket=webSocket;
    }
    @Override
    public void run() {
        List<String> classNames=null;
        try {
            classNames = Files.readAllLines(Paths.get(namesUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Net net = Dnn.readNetFromDarknet(cfgUrl, weightUrl);
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        while (true){
            long startTime=System.currentTimeMillis();
            Boolean flag= (Boolean) redisTemplate.opsForValue().get(videoUrl+""+userId);
            if(!flag){
                break;
            }
            MapTime mapTime=VideoSendReadCfg.matlist.poll();

            if(mapTime!=null){

                log.info("【解析】-当前值{},当前时间{}",mapTime.times,VideoFrameReader.millisecondsToHours(mapTime.times));
                Mat frame=mapTime.mat;
                // 将图像传递给模型进行目标检测
                Mat blob = Dnn.blobFromImage(frame, 1.0 / 255, new Size(416, 416), new Scalar(0), true, false);
                net.setInput(blob);
                // 将图像传递给模型进行目标检测
                List<Mat> result = new ArrayList<>();
                List<String> outBlobNames = net.getUnconnectedOutLayersNames();
                net.forward(result, outBlobNames);


                // 处理检测结果
                float confThreshold = 0.5f;
                List<Rect2d> boundingBoxes = new ArrayList<>();
                List<Float> confidences = new ArrayList<>();
                List<Integer> classIds = new ArrayList<>();
                for (Mat level : result) {
                    for (int i = 0; i < level.rows(); ++i) {
                        Mat row = level.row(i);
                        Mat scores = level.row(i).colRange(5, level.cols());
                        Core.MinMaxLocResult minMaxLocResult = Core.minMaxLoc(scores);
                        Point classIdPoint = minMaxLocResult.maxLoc;
                        double confidence = row.get(0, 4)[0];
                        if (confidence > confThreshold) {
                            //    log.info("classIdPoint"+ classIdPoint);
                            //    log.info("classIdPointx"+ classIdPoint.x);
                            classIds.add((int) classIdPoint.x); //记录标签下标
                            double centerX = row.get(0, 0)[0] * frame.cols();
                            double centerY = row.get(0, 1)[0] * frame.rows();
                            double width = row.get(0, 2)[0] * frame.cols();
                            double height = row.get(0, 3)[0] * frame.rows();
                            double left = centerX - width / 2;
                            double top = centerY - height / 2;

                            // 绘制边界框
                            Rect2d rect = new Rect2d(left, top, width, height);
                            boundingBoxes.add(rect);
                            confidences.add((float)confidence);

                        }
                    }
                }

                // 执行非最大抑制，消除重复的边界框
                MatOfRect2d boxes = new MatOfRect2d(boundingBoxes.toArray(new Rect2d[0]));
                MatOfFloat confidencesMat = new MatOfFloat();
                confidencesMat.fromList(confidences);
                MatOfInt indices = new MatOfInt();
                Dnn.NMSBoxes(boxes, confidencesMat, confThreshold, 0.4f, indices);
                if(indices.empty()){
                    log.info("未识别到视频内容");
                    continue;
                }
                int[]   indicesArray= indices.toArray();
                // 获取保留的边界框

       //         log.info(confidences.size()+"类别下标啊"+indicesArray.length);
                // 在图像上绘制保留的边界框
                int c=0;
                JSONObject bja=new JSONObject();
                List<JSONObject>  jsonlist=new ArrayList<>();
                for (int idx : indicesArray) {
                    Rect2d box = boundingBoxes.get(idx);
                    //    Imgproc.rectangle(frame, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height),CommonColors(c), 2);
                    // 添加类别标签
             //       log.info("当前有多少"+confidences.get(idx));
                    Integer ab=classIds.get(idx);
             //       log.info("类别下标"+ab);
                    //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5))
            //        log.info("Detected object at: (" + box.x + ", " + box.y + "),width: (" + box.width + ", " + box.height + ")");
                    //   Imgproc.putText(frame, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);
                    String name=classNames.get(ab);
                    TabAiBase aiBase =VideoSendReadCfg.map.get(name);
                    bja.put("cmd", "video");
                    JSONObject bj=new JSONObject();
                    bj.put("x", box.x);
                    bj.put("y", box.y);
                    bj.put("width", box.width);
                    bj.put("height", box.height);
                    bj.put("url", videoUrl);
                    bj.put("name", aiBase.getChainName());
                    bj.put("color", aiBase.getCssColor());
                    bj.put("number",mapTime.times);
                    bja.put("number",mapTime.times);
                    jsonlist.add(bj);
                    bja.put("list",jsonlist);
                    c++;

                }
                redisTemplate.opsForValue().set(videoUrl+"time"+userId,mapTime.times,365,TimeUnit.DAYS);
                // 计算跳过的帧数（根据所需的时间消耗）
                webSocket.sendMessage(bja.toJSONString());
                long endTime=System.currentTimeMillis();
                log.error("【解析】-当前解析时间{}---{}整体耗时:{}--{}",mapTime.times,AIModelYolo3.millisecondsToHours(mapTime.times),(endTime-startTime),(endTime-startTime)/1000);
            }

        }
    }
}
