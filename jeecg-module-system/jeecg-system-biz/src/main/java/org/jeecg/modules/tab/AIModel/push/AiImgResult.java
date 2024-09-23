package org.jeecg.modules.tab.AIModel.push;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RestUtil;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.jeecg.modules.tab.AIModel.V5.MapTime;
import org.jeecg.modules.tab.AIModel.V5.VideoFrameReaderV5;
import org.jeecg.modules.tab.AIModel.V5.VideoSendReadCfgV5;
import org.jeecg.modules.tab.AIModel.VideoSendReadCfg;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.entity.pushEntity;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.jeecg.modules.tab.AIModel.AIModelYolo3.*;


/**
 * @author wggg
 * @date 2024/9/19 15:35
 */
@Slf4j
public class AiImgResult implements Runnable {
    static Long LastTime= 0L;
    TabAiSubscription tabAiSubscription;

    Mat matFrame;
    private RedisTemplate redisTemplate;

    String uploadpath="D:\\opt\\upFiles";

    BlockingQueue<Mat> queue;
    List<TabAiModel> aiModel;

    public AiImgResult(List<TabAiModel> aiModel,TabAiSubscription tabAiSubscription, RedisTemplate redisTemplate, BlockingQueue<Mat> queue){
        this.tabAiSubscription=tabAiSubscription;
        this.redisTemplate=redisTemplate;
        this.queue=queue;
        this.aiModel=aiModel;
    }
    @Override
    public void run() {

        while (true){
            try {
                boolean flag= (boolean) redisTemplate.opsForValue().get(tabAiSubscription.getId());
                if(!flag){
                    break;
                }
                Mat mat=queue.take();
                for (TabAiModel tabaiModel: aiModel) {
                    switch (tabaiModel.getSpareOne()) {
                        case "1": //v3
                        {
                            String className=uploadpath+ File.separator +tabaiModel.getAiNameName();
                            String cfgUrl=uploadpath+ File.separator +tabaiModel.getAiConfig();
                            String weightUrl=uploadpath+ File.separator +tabaiModel.getAiWeights();
                            v3(mat, className, cfgUrl, weightUrl,tabaiModel);
                            break;
                        }
                        case "2":{
                            String className=uploadpath+ File.separator +tabaiModel.getAiNameName();
                            String weightUrl=uploadpath+ File.separator +tabaiModel.getAiWeights();
                            V5V8V10(mat, className, weightUrl,tabaiModel);
                            break;
                        }
                    }
                }
                log.info("开始自动识别");

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public void v3(Mat image,String className,String cfgUrl,String weightUrl,TabAiModel tabaiModel){

        Net net = Dnn.readNetFromDarknet(cfgUrl, weightUrl);
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        List<String> classNames=null;
        try {
            classNames = Files.readAllLines(Paths.get(className));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 读取输入图像
        Long a=System.currentTimeMillis();


        // 将图像传递给模型进行目标检测
        Mat blob = Dnn.blobFromImage(image, 1.0 / 255, new Size(416, 416), new Scalar(0), true, false);
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
                    double centerX = row.get(0, 0)[0] * image.cols();
                    double centerY = row.get(0, 1)[0] * image.rows();
                    double width = row.get(0, 2)[0] * image.cols();
                    double height = row.get(0, 3)[0] * image.rows();
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
            log.warn("类别下标啊"+"未识别到内容");
            return;
        }

        int[]   indicesArray= indices.toArray();
        // 获取保留的边界框

        log.info(confidences.size()+"类别下标啊"+indicesArray.length);
        // 在图像上绘制保留的边界框
        int c=0;
        for (int idx : indicesArray) {
            // 添加类别标签

            log.info("当前有多少"+confidences.get(idx));
            Integer ab=classIds.get(idx);
            String name=classNames.get(ab);


            Rect2d box = boundingBoxes.get(idx);
            Imgproc.rectangle(image, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height),CommonColors(c), 2);

            log.info( name+"类别下标"+ab);
            image = AIModelYolo3.addChineseText(image,name,new Point(box.x, box.y - 5),CommonColors(c));
            //  Imgproc.putText(image, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);
            c++;
        }

        String savepath=uploadpath + File.separator + "temp" + File.separator;

        String  saveName=tabAiSubscription.getId()+"_";
        if(StringUtils.isNotBlank(saveName)){
            saveName=savepath+saveName+".jpg";
        }else{
            saveName=savepath+System.currentTimeMillis()+".jpg";
        }
        log.info("存储地址{}",saveName);
        File imageFile = new File(saveName);
        if (imageFile.exists()) {
            imageFile.delete();
        }
        Imgcodecs.imwrite(saveName, image);
        String base64Img=base64Image(saveName);
        //组装参数



        pushEntity push=new  pushEntity();
        push.setVideo(tabAiSubscription.getRemake());
        push.setType("图片");
        push.setCameraUrl(tabAiSubscription.getRemake());
        push.setAlarmPicData(base64Img);
        push.setTime(System.currentTimeMillis()+"");
        push.setModelId(tabaiModel.getId());
        push.setIndexCode(tabAiSubscription.getIndexCode());
        push.setModelName(tabaiModel.getAiName());
        JSONObject ob=null;
        int jg= Integer.parseInt(tabAiSubscription.getEventNumber());
        try {
            Long b=System.currentTimeMillis();
            int endTime= (int) ((b-LastTime)/1000);
            if(LastTime==0L){
                log.info("当前时间未赋值："+endTime);
                LastTime=b;
            }else if(endTime>=jg){
                LastTime=b;
                log.info("当前时间频率赋值："+endTime);
                ob= RestUtil.post(tabAiSubscription.getEventUrl(), (JSONObject) JSONObject.toJSON(push));
            }else if(endTime<jg){
                log.info("当前时间小于间隔："+endTime);

            }

            log.info("消耗时间："+(b-a));
            log.info("返回内容："+ob);
            LastTime=b;



        }catch (Exception ex){
            log.info("连接失败");


        }
    }
    public void V5V8V10( Mat frame,String className,String weightUrl,TabAiModel tabaiModel){
        try {


            List<String> classNames=null;
            try {
                classNames = Files.readAllLines(Paths.get(className));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Net net = Dnn.readNetFromONNX(weightUrl);;
            net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
            net.setPreferableTarget(Dnn.DNN_TARGET_CPU);

                long startTime=System.currentTimeMillis();

                    // 将图像传递给模型进行目标检测
                    Mat blob = Dnn.blobFromImage(frame, 1.0 / 255, new Size(640, 640), new Scalar(0), true, false);
                    net.setInput(blob);
                    // 将图像传递给模型进行目标检测
                    List<Mat> result = new ArrayList<>();
                    List<String> outBlobNames = net.getUnconnectedOutLayersNames();
                    net.forward(result, outBlobNames);


                    // 处理检测结果
                    float confThreshold = 0.3f;
                    float nmsThreshold = 0.4f;
                    List<Rect2d> boxes2d = new ArrayList<>();
                    List<Float> confidences = new ArrayList<>();
                    List<Integer> classIds = new ArrayList<>();

                    for (Mat output : result) {
                        int dims = output.dims();
                        int index = (int) output.size(0);
                        int rows = (int) output.size(1);
                        int cols = (int) output.size(2);
                        // Dims: 3, Rows: 25200, Cols: 8 row,Mat [ 1*25200*8*CV_32FC1, isCont=true, isSubmat=false, nativeObj=0x28dce2da990, dataAddr=0x28dd0ebc640 ]index:1
                        System.out.println("Dims: " + dims + ", Rows: " + rows + ", Cols: " + cols+" row,"+output.row(0)+"index:"+index);
                        Mat detectionMat = output.reshape(1, output.size(1));

                        for (int i = 0; i < detectionMat.rows(); i++) {
                            Mat detection = detectionMat.row(i);
                            Mat scores = detection.colRange(5, cols);
                            Core.MinMaxLocResult minMaxResult = Core.minMaxLoc(scores);
                            float confidence = (float)detection.get(0, 4)[0];
                            Point classIdPoint = minMaxResult.maxLoc;

                            if (confidence > confThreshold) {
                                float centerX = (float)detection.get(0, 0)[0];
                                float centerY = (float)detection.get(0, 1)[0];
                                float width = (float)detection.get(0, 2)[0];
                                float height = (float)detection.get(0, 3)[0];

                                float left = centerX - width / 2;
                                float top = centerY - height / 2;

                                classIds.add((int)classIdPoint.x);
                                confidences.add(confidence);
                                boxes2d.add(new Rect2d(left, top, width, height));
                                //  System.out.println("识别到了");
                            }
                        }
                    }
                    if(boxes2d.size()<=0){
                        log.warn("未识别到内容");
                        return;
                    }
                    // 执行非最大抑制，消除重复的边界框
                    MatOfRect2d boxes_mat = new MatOfRect2d();
                    boxes_mat.fromList(boxes2d);

                    MatOfFloat confidences_mat = new MatOfFloat(Converters.vector_float_to_Mat(confidences));
                    MatOfInt indices = new MatOfInt();
                    Dnn.NMSBoxes(boxes_mat, confidences_mat, confThreshold, nmsThreshold, indices);
                    if (!boxes_mat.empty() && !confidences_mat.empty()) {
                        System.out.println("不为空");
                        Dnn.NMSBoxes(boxes_mat, confidences_mat, confThreshold, nmsThreshold, indices);
                    }
                    int[]   indicesArray= indices.toArray();
                    // 获取保留的边界框
                    int c=0;
                    JSONObject bja=new JSONObject();
                    List<JSONObject>  jsonlist=new ArrayList<>();
                    for (int idx : indicesArray) {
                        Rect2d box = boxes2d.get(idx);
                        Integer ab=classIds.get(idx);
                        float conf = confidences.get(idx);
                        double x=box.x;
                        double y=box.y;
                        double width=box.width*((double)frame.cols()/640);
                        double height=box.height*((double)frame.rows()/640);
                        double xzb=x*((double)frame.cols()/640);
                        double yzb=y*((double)frame.rows()/640);
                        System.out.println("绘制1"+"x:"+x+"y:"+ y+"");
                        System.out.println("绘制1"+"width:"+width+"height:"+ height+"");
                        System.out.println(frame.cols()+" image.cols()"+ Double.valueOf((double)frame.cols()/640));
                        System.out.println(frame.rows()+" image.rows()"+Double.valueOf((double)frame.rows()/640));
                        //       log.info("类别下标"+ab);
                        //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5))
                        //        log.info("Detected object at: (" + box.x + ", " + box.y + "),width: (" + box.width + ", " + box.height + ")");
                        //   Imgproc.putText(frame, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);

                        // TabAiBase aiBase =VideoSendReadCfgV5.map.get(name);

                        c++;

                    }


            String savepath=uploadpath + File.separator + "temp" + File.separator;

            String  saveName=tabAiSubscription.getId();
            if(StringUtils.isNotBlank(saveName)){
                saveName=savepath+saveName+".jpg";
            }else{
                saveName=savepath+System.currentTimeMillis()+".jpg";
            }
            log.info("存储地址{}",saveName);
            File imageFile = new File(saveName);
            if (imageFile.exists()) {
                imageFile.delete();
            }
            Imgcodecs.imwrite(saveName, frame);
            String base64Img=base64Image(saveName);
            //组装参数
            pushEntity push=new  pushEntity();
            push.setVideo(tabAiSubscription.getRemake());
            push.setType("图片");
            push.setCameraUrl(tabAiSubscription.getRemake());
            push.setAlarmPicData(base64Img);
            push.setTime(System.currentTimeMillis()+"");
            push.setModelId(tabaiModel.getId());
            push.setIndexCode(tabAiSubscription.getIndexCode());
            push.setModelName(tabaiModel.getAiName());
            JSONObject ob=null;
            int jg= Integer.parseInt(tabAiSubscription.getEventNumber());
            try {
                Long b = System.currentTimeMillis();
                int endTime = (int) ((b - LastTime) / 1000);
                if (LastTime == 0L) {
                    log.info("当前时间未赋值：" + endTime);
                    LastTime = b;
                } else if (endTime >= jg) {
                    LastTime = b;
                    log.info("当前时间频率赋值：" + endTime);
                    ob = RestUtil.post(tabAiSubscription.getEventUrl(), (JSONObject) JSONObject.toJSON(push));
                } else if (endTime < jg) {
                    log.info("当前时间小于间隔：" + endTime);

                }

                log.info("消耗时间：" + (b - startTime));
                log.info("返回内容：" + ob);
                LastTime = b;


            }catch (Exception ex){
                log.info("连接失败");


            }

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            System.out.println("失败了");
        }
    }

}
