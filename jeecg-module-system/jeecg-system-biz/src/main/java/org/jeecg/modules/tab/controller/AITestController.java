package org.jeecg.modules.tab.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;

import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @date 2024/2/28 10:46
 */
@Api(tags="AI测试类")
@RestController
@RequestMapping("/tab/testAI")
@Slf4j
public class AITestController {

    static Mat image =null;
    private static final double CONFIDENCE_THRESHOLD = 0.5;
    private static final double NMS_THRESHOLD = 0.4;
    private static final Scalar BBOX_COLOR = new Scalar(0, 255, 0);

    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;


    @GetMapping(value = "/testSavePic")
    public Result testSavePic() throws Exception {

        AIModelYolo3 picYoloV3=new AIModelYolo3();

       // String pic=picYoloV3.SavePicInLocalhost("http://img.nj-kj.com/dog.jpg",uploadpath);
        picYoloV3.SendTestVideo();
        return Result.ok("");
    }
    @GetMapping(value = "/testAIModel2")
    public Result testAIModel2() throws Exception {

        // 保存标注后的图像
        AIModelYolo3 picYoloV3=new AIModelYolo3();
        String modelWeights = "F:\\JAVAAI\\yolo3\\yuanshi\\yolov3.weights";
        String modelConfig = "F:\\JAVAAI\\yolo3\\yuanshi\\yolov3.cfg";
        String modelClasses  = "F:\\JAVAAI\\yolo3\\yuanshi\\coco.names";
        String pic="F:\\JAVAAI\\yolo3\\yuanshi\\dog.jpg";
        picYoloV3.SendPicYoloV3(modelWeights,modelConfig,modelClasses,pic,"wangbaba",uploadpath);
        return Result.ok().success("1");
    }


    @GetMapping(value = "/testAIModel")
    public Result testAIModel() throws IOException {
        Long a=System.currentTimeMillis();
        // 加载 YOLOv3 模型
        String modelWeights = "F:\\JAVAAI\\yolo3\\yuanshi\\yolov3.weights";
        String modelConfig = "F:\\JAVAAI\\yolo3\\yuanshi\\yolov3.cfg";
        String modelClasses  = "F:\\JAVAAI\\yolo3\\yuanshi\\coco.names";
        // 加载YOLOv3模型
        Net net = Dnn.readNetFromDarknet(modelConfig, modelWeights);

        // 加载COCO数据集的类别名称
        List<String> classNames = Files.readAllLines(Paths.get(modelClasses));

        // 读取输入图像
        Mat image = Imgcodecs.imread("F:\\JAVAAI\\yolo3\\yuanshi\\dog.jpg");

        // 将图像传递给网络进行前向推断
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
                //    System.out.println("classIdPoint"+ classIdPoint);
                //    System.out.println("classIdPointx"+ classIdPoint.x);
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

        // 获取保留的边界框
        int[] indicesArray = indices.toArray();
        System.out.println(confidences.size()+"类别下标啊"+indicesArray.length);
        // 在图像上绘制保留的边界框
        int c=0;
        for (int idx : indicesArray) {
            Rect2d box = boundingBoxes.get(idx);
            Imgproc.rectangle(image, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height),CommonColors(c), 2);
            // 添加类别标签
            System.out.println("当前有多少"+confidences.get(idx));
            Integer ab=classIds.get(idx);
            System.out.println("类别下标"+ab);
            //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5));
            Imgproc.putText(image, classNames.get(ab), new Point(box.x, box.y - 5), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);
            c++;
        }


        // 保存标注后的图像到本地
        String outputPath = "F:\\JAVAAI\\yolo3\\yuanshi\\test.jpg";
        Imgcodecs.imwrite(outputPath, image);
        Long b=System.currentTimeMillis();
        System.out.println("消耗时间："+(b-a));
        return  Result.OK().success("success"+(b-a)/1000);


    }
    public static Scalar CommonColors(int i){
        Scalar[] commonColors = {
                new Scalar(255, 0, 0),     // 蓝色
                new Scalar(0, 255, 0),     // 绿色
                new Scalar(0, 0, 255),     // 红色
                new Scalar(255, 255, 0),   // 黄色
                new Scalar(0, 255, 255),   // 青色
                new Scalar(255, 0, 255),   // 粉色
                new Scalar(255, 255, 255), // 白色
                new Scalar(0, 0, 0)        // 黑色
                // 添加更多的颜色...
        };
        return commonColors[i];
    }


    @GetMapping(value = "/test")
    public Result StartAITest() throws IOException {
        Long a=System.currentTimeMillis();
        // 加载 YOLOv3 模型
        String modelWeights = "F:\\JAVAAI\\yolo3\\gunzhou\\my_yolov3_final.weights";
        String modelConfig = "F:\\JAVAAI\\yolo3\\gunzhou\\my_yolov3.cfg";
        String modelClasses  = "F:\\JAVAAI\\yolo3\\gunzhou\\myData.names";
        // 加载类别名称
        List<String> classNames =Files.readAllLines(Paths.get(modelClasses));
        // 读取图像
        String imagePath = "F:\\JAVAAI\\test\\00001.jpg"; // 替换为你的图像路径
        // 加载模型
        // 加载模型
        Net net = Dnn.readNetFromDarknet(modelConfig, modelWeights);

        // 读取图像
        image = Imgcodecs.imread(imagePath);

        // 将图像传递给模型进行目标检测
        Mat blob = Dnn.blobFromImage(image, 1.0 / 255.0, new Size(416, 416), new Scalar(0), true, false);
        net.setInput(blob);
        List<Mat> result = new ArrayList<>();
        List<String> outBlobNames = net.getUnconnectedOutLayersNames();
        net.forward(result, outBlobNames);

        // 处理检测结果
        float confThreshold = 0.5f;
        List<Rect2d> boundingBoxes = new ArrayList<>();
        List<Float> confidences = new ArrayList<>();
        for (Mat level : result) {
            for (int i = 0; i < level.rows(); ++i) {
                Mat row = level.row(i);
                double confidence = row.get(0, 4)[0];
                if (confidence > confThreshold) {
                    int classId = (int) row.get(0, 1)[0];
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

        // 获取保留的边界框
        int[] indicesArray = indices.toArray();

        // 在图像上绘制保留的边界框
        for (int idx : indicesArray) {
            Rect2d box = boundingBoxes.get(idx);
            Imgproc.rectangle(image, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height), new Scalar(0, 255, 0), 2);
            String label ="";
            // 添加类别标签


            String caption ="滚轴";
          //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5));
          Imgproc.putText(image, caption, new Point(box.x, box.y - 5), Imgproc.FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(0, 255, 0), 1);
        }


        Imgcodecs.imwrite("F:\\JAVAAI\\wgtest.jpg", image);
        Long b=System.currentTimeMillis();
        System.out.println("消耗时间："+(b-a));
        return  Result.OK().success("success"+(b-a)/1000);
    }
    // 在图像上绘制中文文本

}
