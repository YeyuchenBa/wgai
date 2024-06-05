package org.jeecg.modules.tab.AIModel;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.tab.entity.PushInfo;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.jeecg.modules.demo.tab.service.impl.TabAiSubscriptionServiceImpl;
import org.jeecg.modules.message.websocket.WebSocket;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.VideoWriter;
import org.opencv.videoio.Videoio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import static org.opencv.highgui.HighGui.imshow;


/**
 * @text：
 * @author Wang_java
 * @date 2024/3/13 19:29
 */
@Slf4j
public class AIModelYolo3 {




    /***
     * AI模型嵌套模型
     * 需要绝对路径
     * 输入图片
     */
    public  String SendPicYoloV3(String weight,String cfg,String names,String picUrl,String saveName,String uploadpath) throws Exception {
        log.info(uploadpath);
        Long a=System.currentTimeMillis();
        // 加载类别名称
        List<String> classNames = Files.readAllLines(Paths.get(uploadpath+ File.separator +names));
        // 加载YOLOv3模型
        log.info("cfg地址{}",uploadpath+ File.separator +cfg);
        log.info("weight地址{}",uploadpath+ File.separator +weight);
        Net net = Dnn.readNetFromDarknet(uploadpath+ File.separator +cfg, uploadpath+ File.separator +weight);
        // 读取输入图像
        log.info("图片地址{}",uploadpath+ File.separator +picUrl);
        Mat image = Imgcodecs.imread(uploadpath+ File.separator +picUrl);
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
            return "error";
        }
        int[]   indicesArray= indices.toArray();
        // 获取保留的边界框

        log.info(confidences.size()+"类别下标啊"+indicesArray.length);
        // 在图像上绘制保留的边界框
        int c=0;
        for (int idx : indicesArray) {
            Rect2d box = boundingBoxes.get(idx);
            Imgproc.rectangle(image, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height),CommonColors(c), 2);
            // 添加类别标签
            log.info("当前有多少"+confidences.get(idx));
            Integer ab=classIds.get(idx);
            log.info("类别下标"+ab);
            //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5));
            Imgproc.putText(image, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);
            c++;
        }
        String savepath=uploadpath + File.separator + "temp" + File.separator;

        if(StringUtils.isNotBlank(saveName)){
            savepath+=saveName+".jpg";
        }else{
            saveName=System.currentTimeMillis()+"";
            savepath+=saveName+".jpg";
        }
        log.info(savepath);
        Imgcodecs.imwrite(savepath, image);
        Long b=System.currentTimeMillis();
        log.info("消耗时间："+(b-a));
        return saveName+".jpg";
    }

    /***
     * AI识别输出图片或者视频帧
     * @param weight
     * @param cfg
     * @param names
     * @param videoUrl
     * @param uploadpath
     * @return
     * @throws Exception
     */
    public  String SendVideoYoloV3(String weight,String cfg,String names,String videoUrl,String uploadpath) throws Exception {
        Long a=System.currentTimeMillis();

        // 加载类别名称
        List<String> classNames = Files.readAllLines(Paths.get(uploadpath+ File.separator +names));
        // 加载YOLOv3模型
        log.info("cfg地址{}",uploadpath+ File.separator +cfg);
        log.info("weight地址{}",uploadpath+ File.separator +weight);
        Net net = Dnn.readNetFromDarknet(uploadpath+ File.separator +cfg, uploadpath+ File.separator +weight);
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);
        String savepath=uploadpath + File.separator + a + File.separator;
        File file=new File(savepath);
        if (!file.exists()) {
            file.mkdirs();// 创建文件根目录
        }
        VideoCapture videoCapture=new VideoCapture(videoUrl);
        if (!videoCapture.isOpened()) {
            log.info("未能正确打开视频");
            return "eror";
        }

        // 设置输出视频文件参数
        int frameWidth = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        int frameHeight = (int) videoCapture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
     //   VideoWriter videoWriter = new VideoWriter("F:\\JAVAAI\\output.mp4", VideoCodec.MPEG4, 30, new Size(frameWidth, frameHeight), true);
        VideoWriter videoWriter = new VideoWriter("F:\\JAVAAI\\output.mp4", VideoWriter.fourcc('X', '2', '6', '4'), 30, new Size(frameWidth, frameHeight), true);

        Mat  frame =new Mat();

        while (videoCapture.read(frame)){
            Long b=System.currentTimeMillis();
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
                return "error";
            }
            int[]   indicesArray= indices.toArray();
            // 获取保留的边界框

            log.info(confidences.size()+"类别下标啊"+indicesArray.length);
            // 在图像上绘制保留的边界框
            int c=0;
            for (int idx : indicesArray) {
                Rect2d box = boundingBoxes.get(idx);
                Imgproc.rectangle(frame, new Point(box.x, box.y), new Point(box.x + box.width, box.y + box.height),CommonColors(c), 2);
                // 添加类别标签
                log.info("当前有多少"+confidences.get(idx));
                Integer ab=classIds.get(idx);
                log.info("类别下标"+ab);
                //  AIModelYolo3.addChineseText(image, caption,new Point(box.x, box.y - 5));
                Imgproc.putText(frame, classNames.get(ab), new Point(box.x, box.y - 5), Core.FONT_HERSHEY_SIMPLEX, 0.5, CommonColors(c), 1);
                c++;
                videoWriter.write(frame);
            }

            String  saveName="";
            if(StringUtils.isNotBlank(saveName)){
                saveName=savepath+saveName+".jpg";
            }else{
                saveName=savepath+System.currentTimeMillis()+".jpg";
            }

           // Imgcodecs.imwrite(saveName, frame);


     //       imshow("YOLOv3 Detection", frame);
            long d=(b-a)/1000;
            if(d>60){
                break;
            }
            log.info(saveName+"{}",d);
        }
        // Release resources
        videoCapture.release();
        videoWriter.release();
        return "";

    }




    /***
     * 测试合成是频liu
     *
     */
    public void  SendTestVideo(){
        VideoCapture capture = new VideoCapture();
        long a=System.currentTimeMillis();
        capture.open("http://218.92.168.230:8888/LL/34020000001180000002_34020000001310000005.live.mp4");

        // Check if the capture is opened successfully
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open video stream");
            return;
        }

        // Get video properties
        double frameWidth = capture.get(Videoio.CAP_PROP_FRAME_WIDTH);
        double frameHeight = capture.get(Videoio.CAP_PROP_FRAME_HEIGHT);
        double fps = capture.get(Videoio.CAP_PROP_FPS);

        // Create VideoWriter object to write frames to a file
        String outputFileName = "F:\\JAVAAI\\output_video.mp4"; // Change this to your desired output file name
        int fourcc = VideoWriter.fourcc('X', '2', '6', '4');
        VideoWriter writer = new VideoWriter(outputFileName, fourcc, fps, new Size((int) frameWidth, (int) frameHeight));

        // Process frames from the video stream
        Mat frame = new Mat();
        while (capture.read(frame)) {
            long b=System.currentTimeMillis();
            // Write the frame to the output file
            long c=(b-a)/1000;
            log.info("运行中{}",c);
            writer.write(frame);
            if(c>60){
                break;
            }

        }

        // Release resources
        capture.release();
        writer.release();
    }

    /**
     * 获取坐标
     * @return
     */
    public  String SendVideoLocalhostYoloV3(String userId,String weight,String cfg,String names,String videoUrl,String uploadpath,WebSocket webSocket, RedisUtil redisUtil) throws Exception {
        Long a=System.currentTimeMillis();

        // 加载类别名称
        List<String> classNames = Files.readAllLines(Paths.get(uploadpath+ File.separator +names));
        // 加载YOLOv3模型
        log.info("cfg地址{}",uploadpath+ File.separator +cfg);
        log.info("weight地址{}",uploadpath+ File.separator +weight);
        Net net = Dnn.readNetFromDarknet(uploadpath+ File.separator +cfg, uploadpath+ File.separator +weight);
        net.setPreferableBackend(Dnn.DNN_BACKEND_OPENCV);
        net.setPreferableTarget(Dnn.DNN_TARGET_CPU);

        VideoCapture videoCapture=new VideoCapture(videoUrl);
        if (!videoCapture.isOpened()) {
            log.info("未能正确打开视频");
            return "eror";
        }
        double fps = videoCapture.get(Videoio.CAP_PROP_FPS);
        // 计算每帧的时间消耗（单位：毫秒）
        double frameTime = 1000 / fps; // 单位为毫秒
        log.info("");


        log.info("当前视频帧数{}",fps);
        Mat  frame =new Mat();
        int k=0;
        while (videoCapture.read(frame)){


            Long startTime=System.currentTimeMillis();
            Boolean flag= (Boolean) redisUtil.get(videoUrl+""+userId);
            log.info("获取的当前识别信息{}{}{}",videoUrl,userId,flag);
            if(!flag){
                videoCapture.release();
                break;
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
                bj.put("url", videoUrl);
                bj.put("name", classNames.get(ab));
                bj.put("color", CommonColorsVue(c));
                jsonlist.add(bj);
                bja.put("list",jsonlist);
                c++;

            }



            // 计算跳过的帧数（根据所需的时间消耗）
            webSocket.sendMessage(bja.toJSONString());

            Long b=System.currentTimeMillis();
            long consumingTime=0;
            if(k==0){
                consumingTime=(b-a);
                k++;
            }else{
                consumingTime=(b-startTime);
            }
            int framesToSkip = (int) ( consumingTime / frameTime);

            log.warn("耗时时间{},跳过帧数{}",b-startTime,framesToSkip);
            // 跳过计算出的帧数
//            for (int i = 0; i < framesToSkip; i++) {
//                videoCapture.grab(); // 跳过帧
//            }

            Mat newmat=new Mat();
            VideoCapture videoCapture2=new VideoCapture(videoUrl);
            videoCapture2.read(newmat);
            videoCapture.release();
            long timestamp2 = (long) videoCapture2.get(Videoio.CAP_PROP_POS_MSEC);
            videoCapture=videoCapture2;


            long timestamp = (long) videoCapture.get(Videoio.CAP_PROP_POS_MSEC);
            log.warn("当前帧时间"+(millisecondsToHours(timestamp)));
            log.warn("最新帧时间"+(millisecondsToHours(timestamp2)));
        }

        return "";

    }

    /**
     * 多线程处理视频帧
     * @param
     * @return
     */
    public  String SendVideoLocalhostYoloV3Thread(String userId, String weight, String cfg, String names, String videoUrl, String uploadpath, WebSocket webSocket, RedisUtil redisUtil, RedisTemplate redisTemplate) throws Exception {
        Long a=System.currentTimeMillis();


        // 加载YOLOv3模型
        log.info("cfg地址{}",uploadpath+ File.separator +cfg);
        log.info("weight地址{}",uploadpath+ File.separator +weight);
        log.info("names{}",uploadpath+ File.separator +names);
        // 计算每帧的时间消耗（单位：毫秒）
        int maxIdleThreads = Runtime.getRuntime().availableProcessors();
        log.info("当前主机最大空闲线程数：" + maxIdleThreads);
        // 创建线程池
        ExecutorService executor = Executors.newCachedThreadPool();
        VideoSendReadCfg.StartTime=0;
        log.info("videoUrl：" + videoUrl);
        // 提交多个任务到线程池
        for (int i = 0; i < 3; i++) {
            //效果延迟了三秒
       //     executor.submit(new VideoFrameReader(videoUrl,uploadpath+ File.separator +weight,uploadpath+ File.separator +cfg,uploadpath+ File.separator +names,redisUtil,webSocket,userId,i,redisTemplate));
            if(i==0){
                executor.submit(new VideoRead(videoUrl,redisTemplate,userId));
            }else if(i==1){
                executor.submit(new VideoReadInfo(videoUrl,redisTemplate,userId,uploadpath+ File.separator +names,uploadpath+ File.separator +cfg,uploadpath+ File.separator +weight,webSocket));
            }else{
                executor.submit(new VideoReadtest(videoUrl,redisTemplate,userId));
            }
            Thread.sleep(500);
        }
        // 关闭线程池
        executor.shutdown();



        return "";

    }

    /***
     * 带线程推送
     *
     * @return
     */
    public void SendPicThread(RedisTemplate redisTemplate){
        List<PushInfo> pushA= (List<PushInfo> ) redisTemplate.opsForValue().get("sendPush");
        ExecutorService executor = Executors.newCachedThreadPool();
        for (PushInfo pushInfo:pushA) {
            executor.submit(new VideoReadPic(pushInfo));
        }


    }

    /**
     * 图片转base64
     * @param imagePath
     * @return
     */
    public static String base64Image(String imagePath){
        try {
            // 读取图片文件
            File file = new File(imagePath);
            byte[] bytesArray = new byte[(int) file.length()];
            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); // 读取文件内容到字节数组
            fis.close();

            // 将字节数组编码为Base64字符串
            String base64String = Base64.getEncoder().encodeToString(bytesArray);

            return base64String;
        } catch (IOException e) {
            e.printStackTrace();
            return "图片解析错误";
        }

    }

    public static String millisecondsToHours(long milliseconds) {
        // 将毫秒转换为小时、分钟和秒
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        // 构造结果字符串
        String result = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        return result;
    }


    public static Mat image=new Mat();
    public static Mat addChineseText(Mat images, String text, Point position,Scalar scalar) {
        BufferedImage bufferedImage = matToBufferedImage(images);
        Graphics graphics = bufferedImage.getGraphics();

        // 设置中文文本字体
        Font chineseFont = new Font("微软雅黑", Font.PLAIN, 26);
        graphics.setFont(chineseFont);
        Color awtColor = convertScalarToColor(scalar);
        // 设置文本颜色
        graphics.setColor(awtColor);

        // 在指定位置绘制中文文本
        graphics.drawString(text, (int) position.x, (int) position.y);

        // 将修改后的图像转换回OpenCV的Mat对象
        return bufferedImageToMat(bufferedImage);
    }

    private static Color convertScalarToColor(Scalar scalarColor) {
        double[] rgb = scalarColor.val;
        int r = (int) rgb[2];
        int g = (int) rgb[1];
        int b = (int) rgb[0];
        return new Color(r, g, b);
    }
    // 将Mat对象转换为BufferedImage对象
    public static BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }

    // 将BufferedImage对象转换为Mat对象
    public static Mat bufferedImageToMat(BufferedImage image) {
        byte[] pixels = ((java.awt.image.DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }


    /***
     * 保存线上图片到本地
     * @param imageUrl
     * @return
     */
    public String SavePicInLocalhost(String imageUrl,String path){

        try {
            String uuid =System.currentTimeMillis()+"";
            File dir = new File(path+File.separator);
            if(!dir.exists()) {
                if (!dir.mkdirs()) {
                    dir.mkdirs();// 创建文件根目录
                }
            }

            // 打开连接
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            // 设置请求超时为15秒
            connection.setConnectTimeout(15 * 1000);
            // 读取数据流并保存到本地
            InputStream input = connection.getInputStream();
            byte[] datas = new byte[2048];
            int len;
            FileOutputStream output = new FileOutputStream(new File(dir, uuid + ".jpg"));
            while ((len = input.read(datas)) != -1) {
                output.write(datas, 0, len);
            }
            output.close();
            input.close();
            log.info("图片保存成功：" + dir + uuid + ".jpg");
            return  uuid+".jpg";
        } catch (IOException e) {

            log.info("图片保存失败：" + e.getMessage());
            return  "error";
        }

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
                new Scalar(0, 0, 0),        // 黑色
                new Scalar(128, 128, 128), // 灰色
                new Scalar(250,128,114), // 三文鱼
                new Scalar(240,128,128), // 珊瑚
                new Scalar(255,99,71), // 番茄
                new Scalar(124,252,0), // 草坪绿
                new Scalar(72,209,204), // 绿松石色
                new Scalar(0,206,209), // 深蓝绿色
                new Scalar(65,105,225), // 宝蓝色
                new Scalar(255,0,255), // 紫红色
                new Scalar(255, 0, 0),     // 蓝色
                new Scalar(0, 255, 0),     // 绿色
                new Scalar(0, 0, 255),     // 红色
                new Scalar(255, 255, 0),   // 黄色
                new Scalar(0, 255, 255),   // 青色
                new Scalar(255, 0, 255),   // 粉色
                new Scalar(255, 255, 255), // 白色
                new Scalar(0, 0, 0),        // 黑色
                new Scalar(128, 128, 128), // 灰色
                new Scalar(250,128,114), // 三文鱼
                new Scalar(240,128,128), // 珊瑚
                new Scalar(255,99,71), // 番茄
                new Scalar(124,252,0), // 草坪绿
                new Scalar(72,209,204), // 绿松石色
                new Scalar(0,206,209), // 深蓝绿色
                new Scalar(65,105,225), // 宝蓝色
                new Scalar(255,0,255), // 紫红色
                new Scalar(255, 0, 0),     // 蓝色
                new Scalar(0, 255, 0),     // 绿色
                new Scalar(0, 0, 255),     // 红色
                new Scalar(255, 255, 0),   // 黄色
                new Scalar(0, 255, 255),   // 青色
                new Scalar(255, 0, 255),   // 粉色
                new Scalar(255, 255, 255), // 白色
                new Scalar(0, 0, 0),        // 黑色
                new Scalar(128, 128, 128), // 灰色
                new Scalar(250,128,114), // 三文鱼
                new Scalar(240,128,128), // 珊瑚
                new Scalar(255,99,71), // 番茄
                new Scalar(124,252,0), // 草坪绿
                new Scalar(72,209,204), // 绿松石色
                new Scalar(0,206,209), // 深蓝绿色
                new Scalar(65,105,225), // 宝蓝色
                new Scalar(255,0,255), // 紫红色
                // 添加更多的颜色...
        };
        if(i>=commonColors.length){
            i=0;
        }
        return commonColors[i];
    }
    public static String CommonColorsVue(int i){
        String [] commonColors = {
                "#0000FF",     // 蓝色
                "#00FF00",     // 绿色
                "#FF0000",     // 红色
                "#FFFF00",   // 黄色
                "#00FFFF",   // 青色
                "#FFC0CB",   // 粉色
                "#FFFFFF", // 白色
                "#000000",        // 黑色
                "#808080", // 灰色
                "#FA8072", // 三文鱼
                "#FF7F50", // 珊瑚
                "#FF6347", // 番茄
                "#7CFC00", // 草坪绿
                "#48D1CC", // 绿松石色
                "#00CED1", // 深蓝绿色
                "#4169E1", // 宝蓝色
                "#FF00FF", // 紫红色
                "#0000FF",     // 蓝色
                "#00FF00",     // 绿色
                "#FF0000",     // 红色
                "#FFFF00",   // 黄色
                "#00FFFF",   // 青色
                "#FFC0CB",   // 粉色
                "#FFFFFF", // 白色
                "#000000",        // 黑色
                "#808080", // 灰色
                "#FA8072", // 三文鱼
                "#FF7F50", // 珊瑚
                "#FF6347", // 番茄
                "#7CFC00", // 草坪绿
                "#48D1CC", // 绿松石色
                "#00CED1", // 深蓝绿色
                "#4169E1", // 宝蓝色
                "#FF00FF", // 紫红色
                "#0000FF",     // 蓝色
                "#00FF00",     // 绿色
                "#FF0000",     // 红色
                "#FFFF00",   // 黄色
                "#00FFFF",   // 青色
                "#FFC0CB",   // 粉色
                "#FFFFFF", // 白色
                "#000000",        // 黑色
                "#808080", // 灰色
                "#FA8072", // 三文鱼
                "#FF7F50", // 珊瑚
                "#FF6347", // 番茄
                "#7CFC00", // 草坪绿
                "#48D1CC", // 绿松石色
                "#00CED1", // 深蓝绿色
                "#4169E1", // 宝蓝色
                "#FF00FF", // 紫红色
                "#0000FF",     // 蓝色
                "#00FF00",     // 绿色
                "#FF0000",     // 红色
                "#FFFF00",   // 黄色
                "#00FFFF",   // 青色
                "#FFC0CB",   // 粉色
                "#FFFFFF", // 白色
                "#000000",        // 黑色
                "#808080", // 灰色
                "#FA8072", // 三文鱼
                "#FF7F50", // 珊瑚
                "#FF6347", // 番茄
                "#7CFC00", // 草坪绿
                "#48D1CC", // 绿松石色
                "#00CED1", // 深蓝绿色
                "#4169E1", // 宝蓝色
                "#FF00FF", // 紫红色
                "#0000FF",     // 蓝色
                "#00FF00",     // 绿色
                "#FF0000",     // 红色
                "#FFFF00",   // 黄色
                "#00FFFF",   // 青色
                "#FFC0CB",   // 粉色
                "#FFFFFF", // 白色
                "#000000",        // 黑色
                "#808080", // 灰色
                "#FA8072", // 三文鱼
                "#FF7F50", // 珊瑚
                "#FF6347", // 番茄
                "#7CFC00", // 草坪绿
                "#48D1CC", // 绿松石色
                "#00CED1", // 深蓝绿色
                "#4169E1", // 宝蓝色
                "#FF00FF", // 紫红色


                // 添加更多的颜色...
        };
        if(i>=commonColors.length){
            i=0;
        }
        return commonColors[i];
    }
    // 生成指定数量的随机颜色
    private static Scalar[] generateRandomColors(int count) {
        Scalar[] colors = new Scalar[count];
        for (int i = 0; i < count; i++) {
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            colors[i] = new Scalar(b, g, r);
        }
        return colors;
    }
}
