package org.jeecg.modules.tab.AIModel.V5;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wg
 * @date 2024/3/21 17:58
 */
@Slf4j
public class VideoFrameReaderV5 implements Runnable {


    private String videoStreamURL;
    private String weightUrl;
    private String cfgUrl;
    private String namesUrl;
    private RedisUtil redisUtil;
    private WebSocket webSocket;
    private String userId;
    private  Integer number;
    private RedisTemplate redisTemplate;
    public VideoFrameReaderV5(String videoStreamURL, String weightUrl, String cfgUrl, String namesUrl, RedisUtil redisUtil, WebSocket webSocket, String userId, int number, RedisTemplate redisTemplate) {
        this.videoStreamURL = videoStreamURL;
        this.weightUrl = weightUrl;
        this.cfgUrl = cfgUrl;
        this.namesUrl = namesUrl;
        this.redisUtil = redisUtil;
        this.webSocket = webSocket;
        this.userId=userId;
        this.number=number;
        this.redisTemplate=redisTemplate;
    }

    @Override
    public void run() {
        VideoCapture capture = new VideoCapture(videoStreamURL);
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open video stream.");
            return;
        }
        // 尝试调整缓冲区大小
        capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧
        List<String> classNames=null;
        try {
             classNames = Files.readAllLines(Paths.get(namesUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Net net = Dnn.readNetFromDarknet(cfgUrl, weightUrl);
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        Mat frame = new Mat();
        double fps = capture.get(Videoio.CAP_PROP_FPS);

        while (capture.read(frame)) {
            long startTime=System.currentTimeMillis();
            log.info("当前线程数：{},当前视频帧FPS{},当前视时间毫秒{}",number,fps,startTime);
            Boolean flag= (Boolean) redisTemplate.opsForValue().get(videoStreamURL+""+userId);
            if(!flag){
                capture.release();
                break;
            }
            Integer AITime= (Integer) redisTemplate.opsForValue().get(videoStreamURL+"timeV5"+userId);
            long timestamp2=0;
            if(AITime==null){
                log.error("当前未存储时间节点 当前线程数{}",number);
                long timestamp = (long) capture.get(Videoio.CAP_PROP_POS_MSEC);
                redisTemplate.opsForValue().setIfAbsent(videoStreamURL+"timeV5"+userId,timestamp,365, TimeUnit.DAYS);
                continue;
            }else{

                // 设置视频捕获对象的帧索引为最新帧
               long timestamp = (long) capture.get(Videoio.CAP_PROP_POS_MSEC);

               log.warn("Redis时间戳{}--{}----当前时间戳{}--{}当前线程数{}",AITime,millisecondsToHours(AITime),timestamp,millisecondsToHours(timestamp),number);
               if(AITime<timestamp){

                     log.error("未赋值后的时间戳{},当前线程数{}",AITime,number);

                     redisTemplate.delete(videoStreamURL+"timeV5"+userId);
                     redisTemplate.opsForValue().setIfAbsent(videoStreamURL+"timeV5"+userId,timestamp,365, TimeUnit.DAYS);
                   //更新为最新时间戳
                   VideoCapture videoCapture2=new VideoCapture(videoStreamURL);
                   videoCapture2.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧
                   videoCapture2.read(frame);
                   timestamp2= (long) videoCapture2.get(Videoio.CAP_PROP_POS_MSEC);
                   capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧
                   capture.release();
                   capture=videoCapture2;
                   videoCapture2.release();

                    log.info(AITime+"赋值后的时间戳{}--{},当前线程数{}",timestamp,millisecondsToHours(timestamp),number);
                }else{
                    continue;
                }
           }

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

            log.info(confidences.size()+"类别下标啊"+indicesArray.length);
            // 在图像上绘制保留的边界框
            int c=0;
            JSONObject bja=new JSONObject();
            List<JSONObject>  jsonlist=new ArrayList<>();
            for (int idx : indicesArray) {
                Rect2d box = boundingBoxes.get(idx);
                //    Imgproc.rectangle(frame, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height),CommonColors(c), 2);
                // 添加类别标签
                log.info("当前有多少"+confidences.get(idx));
                Integer ab=classIds.get(idx);
                log.info("类别下标"+ab);
                //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5))
                log.info("Detected object at: (" + box.x + ", " + box.y + "),width: (" + box.width + ", " + box.height + ")");
                //   Imgproc.putText(frame, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);

                bja.put("cmd", "video");
                JSONObject bj=new JSONObject();
                bj.put("x", box.x);
                bj.put("y", box.y);
                bj.put("width", box.width);
                bj.put("height", box.height);
                bj.put("url", videoStreamURL);
                bj.put("name", classNames.get(ab));
                bj.put("color", AIModelYolo3.CommonColorsVue(c));
                bja.put("number",timestamp2);
                jsonlist.add(bj);
                bja.put("list",jsonlist);
                c++;

            }



            // 计算跳过的帧数（根据所需的时间消耗）
            webSocket.sendMessage(bja.toJSONString());
            long endTime=System.currentTimeMillis();
            log.error("整体耗时:{}--{}",(endTime-startTime),(endTime-startTime)/1000);
        }

        capture.release();
    }
    public static String millisecondsToHours(long milliseconds) {
        // 将毫秒转换为小时、分钟和秒
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        long millis = milliseconds % 1000;
        // 构造结果字符串
        String result = String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds,millis);
        return result;
    }
}
