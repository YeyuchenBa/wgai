package org.jeecg.modules.tab.AIModel.V5;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.opencv.core.*;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.utils.Converters;
import org.opencv.videoio.VideoCapture;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wggg
 * @date 2024/7/19 16:08
 */
public class test {

    public static void main(String[] args) {
        System.load("F:\\JAVAAI\\opencv481\\opencv\\build\\java\\x64\\opencv_java481.dll");
        String videoStreamURL="http://218.92.168.230:8888/rtp/32092409401187020001_32092409401327020008.live.mp4";
        VideoCapture capture = new VideoCapture(videoStreamURL);
        String namesUrl="D:\\opt\\upFiles\\temp\\v5jichu_1721372113766.txt";
        String weightUrl="D:\\opt\\upFiles\\temp\\yolov5s_1721371857932.onnx";
        List<String> classNames=null;
        try {
            classNames = Files.readAllLines(Paths.get(namesUrl));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Net net = Dnn.readNetFromONNX(weightUrl);;
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);

        Mat frame = new Mat();
        while (capture.read(frame)) {
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
                System.out.println(" image.cols()"+ Double.valueOf((double)frame.cols()/640));
                System.out.println(" image.rows()"+Double.valueOf((double)frame.rows()/640));
                //       log.info("类别下标"+ab);
                //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5))
                //        log.info("Detected object at: (" + box.x + ", " + box.y + "),width: (" + box.width + ", " + box.height + ")");
                //   Imgproc.putText(frame, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);
                String name=classNames.get(ab);
                TabAiBase aiBase =VideoSendReadCfgV5.map.get(name);
                bja.put("cmd", "video");
                JSONObject bj=new JSONObject();
                bj.put("x", xzb);
                bj.put("y", yzb);
                bj.put("width", width);
                bj.put("height",height);
                bj.put("url", "");

                bj.put("number","1");
                bja.put("number","2");
                jsonlist.add(bj);
                bja.put("list",jsonlist);
                c++;

            }

            // 计算跳过的帧数（根据所需的时间消耗）

            long endTime=System.currentTimeMillis();
           // log.error("【解析】-当前解析时间{}---{}整体耗时:{}--{}",mapTime.times, AIModelYolo3.millisecondsToHours(mapTime.times),(endTime-startTime),(endTime-startTime)/1000);
        }
    }
}