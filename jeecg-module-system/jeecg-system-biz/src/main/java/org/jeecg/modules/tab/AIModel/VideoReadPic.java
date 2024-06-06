package org.jeecg.modules.tab.AIModel;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RestUtil;
import org.jeecg.modules.demo.tab.entity.PushInfo;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.entity.pushEntity;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.jeecg.modules.tab.AIModel.AIModelYolo3.CommonColors;
import static org.jeecg.modules.tab.AIModel.AIModelYolo3.base64Image;

/**
 * @author Administrator
 * @date 2024/4/9 14:33
 */
@Slf4j
public class VideoReadPic implements Runnable{

    static String uploadpath="opt\\upFiles";
    static PushInfo pushInfo;
    static Long LastTime= 0L;

    public VideoReadPic(PushInfo pushInfo){
        this.pushInfo=pushInfo;

    }

    @Override
    public void run() {
        log.info("开始识别！！！！！！！！！！！！！！！！！！！！！！！！！！！！"+pushInfo.getPushUrl());
        List<TabAiModel> tabAiModels=pushInfo.getTabAiModelList();
        List<Net> nets = new ArrayList<>();
        List<String> claseeNames=new ArrayList<>();
        for (TabAiModel tabAiModel:tabAiModels) {
            Net net = Dnn.readNetFromDarknet(uploadpath+ File.separator +tabAiModel.getAiConfig(), uploadpath+ File.separator +tabAiModel.getAiWeights());
            nets.add(net);
            claseeNames.add(uploadpath+ File.separator +tabAiModel.getAiNameName());
        }


        Mat frame = new Mat();
        VideoCapture capture = new VideoCapture(pushInfo.getVideoURL());
        if (!capture.isOpened()) {
            log.info("Error: Unable to open video file.");
        }
        while (capture.read(frame)){
            log.info("循环识别中！！！！！！！！！！！！！！！！！！！！！！！！！！！！"+pushInfo.getPushUrl());
            for (int i = 0; i < nets.size(); i++) {
                detectObjects(frame, nets.get(i), claseeNames.get(i),tabAiModels.get(i));

            }

        }
        capture.release();
        // 释放资源

//        Mat frame = Imgcodecs.imread(uploadpath+ File.separator +"wggggs_1710503896835.png");
//        for (int i = 0; i < nets.size(); i++) {
//            detectObjects(frame, nets.get(i), claseeNames.get(i));
//        }
    }

    private static boolean detectObjects(Mat image, Net net, String className,TabAiModel tabAiModel) {

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
            log.info("类别下标啊"+"未识别到内容");
            return false;
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
            TabAiBase aiBase =VideoSendReadCfg.map.get(name);
            if(aiBase==null){

            }
            Rect2d box = boundingBoxes.get(idx);
            Imgproc.rectangle(image, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height),CommonColors(c), 2);

            log.info( aiBase.getChainName()+"类别下标"+ab);
             image = AIModelYolo3.addChineseText(image, aiBase.getChainName(),new Point(box.x, box.y - 5),CommonColors(c));
          //  Imgproc.putText(image, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);
            c++;
        }

        String savepath=uploadpath + File.separator + "temp" + File.separator;

        String  saveName=pushInfo.getPushId();
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
       push.setVideo(pushInfo.getVideoURL());
       push.setType("图片");
       push.setCameraUrl(pushInfo.getVideoURL());
       push.setAlarmPicData(base64Img);
       push.setTime(System.currentTimeMillis()+"");
       push.setModelId(tabAiModel.getId());
       push.setIndexCode(pushInfo.getIndexCode());
       push.setModelName(tabAiModel.getAiName());
        JSONObject ob=null;
        try {
            Long b=System.currentTimeMillis();
            int endTime= (int) ((b-LastTime)/1000);
            if(LastTime==0L){
                log.info("当前时间未赋值："+endTime);
                LastTime=b;
            }else if(endTime>=pushInfo.getTime()){
                LastTime=b;
                log.info("当前时间频率赋值："+endTime);
            }else if(endTime<pushInfo.getTime()){
                log.info("当前时间小于间隔："+endTime);
                return  false;
            }
            ob=RestUtil.post(pushInfo.getPushUrl(), (JSONObject) JSONObject.toJSON(push));
            log.info("消耗时间："+(b-a));
            log.info("返回内容："+ob);
            LastTime=b;



       }catch (Exception ex){
            log.info("连接失败");
            return false;

       }


       return true;
    }
}
