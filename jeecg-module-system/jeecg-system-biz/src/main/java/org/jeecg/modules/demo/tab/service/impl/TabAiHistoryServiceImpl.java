package org.jeecg.modules.demo.tab.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.demo.tab.entity.TabAiHistory;
import org.jeecg.modules.demo.tab.entity.TabAiModelBund;
import org.jeecg.modules.demo.tab.mapper.TabAiBaseMapper;
import org.jeecg.modules.demo.tab.mapper.TabAiHistoryMapper;
import org.jeecg.modules.demo.tab.service.ITabAiHistoryService;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.monitor.service.RedisService;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.jeecg.modules.tab.AIModel.VideoSendReadCfg;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.mapper.TabAiModelMapper;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.jeecg.modules.tab.AIModel.AIModelYolo3.bufferedImageToMat;
import static org.jeecg.modules.tab.AIModel.AIModelYolo3.converterToFrame;

/**
 * @Description: AI识别结果历史
 * @Author: jeecg-boot
 * @Date:   2024-03-13
 * @Version: V1.0
 */
@Service
@Slf4j
public class TabAiHistoryServiceImpl extends ServiceImpl<TabAiHistoryMapper, TabAiHistory> implements ITabAiHistoryService {

    @Autowired
    TabAiBaseMapper tabAiBaseMapper;
    @Autowired
    TabAiModelMapper modelMapper;
    @Autowired
    TabAiHistoryMapper tabAiHistoryMapper;

    @Resource
    WebSocket webSocket;
    @Resource
    RedisUtil redisUtil;

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public int saveCarIdentify(TabAiModelBund tabAiModelBund, String path) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendPicOpencvCar(tabAiModel1.getAiWeights(),tabAiModelBund.getSaveUrl(),null,path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }

    }

    @Override
    public int saveIdentify(TabAiModelBund tabAiModelBund,String path) {

        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
           String savePath=modelYolo3.SendPicYoloV3(tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSaveUrl(),null,path);
           if(savePath.equals("error")){
               return 1;
           }
           log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }

    }

    @Override
    public int saveIdentifyYolov5(TabAiModelBund tabAiModelBund, String path) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendPicYoloV5(tabAiModel1.getAiWeights(),tabAiModel1.getAiNameName(),tabAiModelBund.getSaveUrl(),null,path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
    }

    @Override
    public int saveIdentifyYolov8(TabAiModelBund tabAiModelBund, String path) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendPicYoloV5(tabAiModel1.getAiWeights(),tabAiModel1.getAiNameName(),tabAiModelBund.getSaveUrl(),null,path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
    }

    @Override
    public int closedentify(TabAiModelBund tabAiModelBund, LoginUser sysUser) {
        //使用redis 设置关闭当前流 使用的用户关闭识别 超时时间设置3s失效


        TabAiModel aiModel=modelMapper.selectById(tabAiModelBund.getModelName());
        String vtype = "";
        String vTime = "time";
        if(aiModel!=null) {
            switch (aiModel.getSpareOne()) {
                case "1": //v3
                {
                    log.info("啥也没有进入");
                }
                case "2"://v5
                case "3"://v8
                {
                    vtype = "V5";
                    vTime = "timeV5";
                }
            }
        }
        redisUtil.set(tabAiModelBund.getSendUrl()+vtype+sysUser.getId(),false);
        redisUtil.expire(tabAiModelBund.getSendUrl()+vtype+sysUser.getId(),3000);
        //
        redisUtil.set(tabAiModelBund.getSendUrl()+vTime+sysUser.getId(),0);
        redisUtil.expire(tabAiModelBund.getSendUrl()+vTime+sysUser.getId(),3000);
        return 0;
    }

    @Override
    public int saveIdentifyVideo(TabAiModelBund tabAiModelBund, String path) {

        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendVideoYoloV3(tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
    }

    @Override
    public int saveIdentifyLocalVideo(TabAiModelBund tabAiModelBund, String path,String userId) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        redisUtil.set(tabAiModelBund.getSendUrl()+""+userId,true);
        redisUtil.expire(tabAiModelBund.getSendUrl()+""+userId,( 24*60*60*365*1000));
        try {
            String savePath=modelYolo3.SendVideoLocalhostYoloV3(userId,tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path,webSocket,redisUtil);
            if(savePath.equals("error")){
                return 1;
            }
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public int saveIdentifyLocalVideoThread(TabAiModelBund tabAiModelBund, String path, String userId) {

        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        //处置结束符
        redisUtil.set(tabAiModelBund.getSendUrl()+""+userId,true);
        redisUtil.expire(tabAiModelBund.getSendUrl()+""+userId,( 24*60*60*365*1000));
        //处置时间戳
        redisUtil.set(tabAiModelBund.getSendUrl()+"time"+userId,0);
        redisUtil.expire(tabAiModelBund.getSendUrl()+"time"+userId,( 24*60*60*365*1000));
        //处置基础库
        Object object = redisUtil.get("AIModelBase");
        if(object==null){
            LambdaQueryWrapper< TabAiBase> querybase = new LambdaQueryWrapper<>();
            List<TabAiBase> base=tabAiBaseMapper.selectList(querybase);
            redisUtil.set("AIModelBase", JSONObject.toJSONString(base));
            redisUtil.expire("AIModelBase",(24*60*60*365*1000));
            VideoSendReadCfg.map=VideoSendReadCfg.getMap(base);
        }else{
            String jsonObject= object.toString();
            JSONArray array=JSONArray.parseArray(jsonObject);
            List<TabAiBase> base= array.toJavaList(TabAiBase.class);
            VideoSendReadCfg.map=VideoSendReadCfg.getMap(base);
        }
        try {

            String savePath=modelYolo3.SendVideoLocalhostYoloV3Thread(userId,tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path,webSocket,redisUtil,redisTemplate);
            if(savePath.equals("error")){
                return 1;
            }
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public int saveIdentifyLocalVideoThreadV5(TabAiModelBund tabAiModelBund, String path, String userId) {
        log.info("V5识别内容开始！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        //处置结束符
        redisUtil.set(tabAiModelBund.getSendUrl()+"V5"+userId,true);
        redisUtil.expire(tabAiModelBund.getSendUrl()+"V5"+userId,( 24*60*60*365*1000));
        //处置时间戳
        redisUtil.set(tabAiModelBund.getSendUrl()+"timeV5"+userId,0);
        redisUtil.expire(tabAiModelBund.getSendUrl()+"timeV5"+userId,( 24*60*60*365*1000));
        //处置基础库
        Object object = redisUtil.get("AIModelBaseV5");
        if(object==null){
            LambdaQueryWrapper< TabAiBase> querybase = new LambdaQueryWrapper<>();
            List<TabAiBase> base=tabAiBaseMapper.selectList(querybase);
            redisUtil.set("AIModelBaseV5", JSONObject.toJSONString(base));
            redisUtil.expire("AIModelBaseV5",(24*60*60*365*1000));
            VideoSendReadCfg.map=VideoSendReadCfg.getMap(base);
        }else{
            String jsonObject= object.toString();
            JSONArray array=JSONArray.parseArray(jsonObject);
            List<TabAiBase> base= array.toJavaList(TabAiBase.class);
            VideoSendReadCfg.map=VideoSendReadCfg.getMap(base);
        }
        try {

            String savePath=modelYolo3.SendVideoLocalhostYoloV5Thread(userId,tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path,webSocket,redisUtil,redisTemplate);
            if(savePath.equals("error")){
                return 1;
            }
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public Result<String>  startAi(TabAiModelBund tabAiModelBund, String path, String userId) {


        TabAiModel aiModel=modelMapper.selectById(tabAiModelBund.getModelName());
        if(aiModel!=null){


            switch (aiModel.getSpareOne()){
                case "1": //v3
                {
                    log.info("【进入V3开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceOne().equals("0")){ //当前为图片
                        int a=this.saveIdentify(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }else{
                        // 输出视频
                        // tabAiHistoryService.saveIdentifyVideo(tabAiModelBund,uploadpath);
                        // 输出坐标 延迟3-5s
                        //tabAiHistoryService.saveIdentifyLocalVideo(tabAiModelBund,uploadpath,sysUser.getId());
                        //多线程输出坐标
                        this.saveIdentifyLocalVideoThread(tabAiModelBund,path,userId);
                        return Result.OK("视频识别开始");
                    }
                }
                case "2":
                {
                    log.info("【进入V5开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceOne().equals("0")){ //当前为图片
                        int a=this.saveIdentifyYolov5(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }else{
                        // 输出视频
                        // tabAiHistoryService.saveIdentifyVideo(tabAiModelBund,uploadpath);
                        // 输出坐标 延迟3-5s
                        //tabAiHistoryService.saveIdentifyLocalVideo(tabAiModelBund,uploadpath,sysUser.getId());
                        //多线程输出坐标
                        this.saveIdentifyLocalVideoThreadV5(tabAiModelBund,path,userId);
                        return Result.OK("视频识别开始");
                    }

                }//v5
                case "3":
                {  log.info("【进入V8开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    log.info("【进入V5开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceOne().equals("0")){ //当前为图片
                        int a=this.saveIdentifyYolov8(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }else{
                        // 输出视频
                        // tabAiHistoryService.saveIdentifyVideo(tabAiModelBund,uploadpath);
                        // 输出坐标 延迟3-5s
                        //tabAiHistoryService.saveIdentifyLocalVideo(tabAiModelBund,uploadpath,sysUser.getId());
                        //多线程输出坐标
                        this.saveIdentifyLocalVideoThread(tabAiModelBund,path,userId);
                        return Result.OK("视频识别开始");
                    }
                }//v8
                case "4": {}//json
                case "5": {}//other
                case "6": { //cv
                    log.info("【进入cv开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceTwo().equals("车牌识别")){
                        int a=this.saveCarIdentify(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }
                }//json
            }



        }
        return Result.error("识别失败未发现识别内容");
    }

    @Override
    public void sendUrl() {
        log.info("进入：！！！！！！！！");

        //   SendPicYoloV3("yolov3.weights","yolov3.cfg","coco.names","car.jpg","test","F:\\JAVAAI\\yolo3\\yuanshi");
        //     SendPicYoloV5("NBplate.onnx","coco.names","writecat.jpg","","F:\\JAVAAI\\yolov5");
        //    SendPicYoloV5Car("NBplate.onnx","coco.names","bluecar.jpg","","F:\\JAVAAI\\yolov5");
        String rtspUrl="rtsp://admin:ch255899@192.168.0.200/Streaming/Channels/102";
//        String rtspUrl = "rtsp://[用户名]:[密码]@[IP地址]:[端口]/[码流类型]";
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl);
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        VideoWriter videoWriter = new VideoWriter();
        try {
            grabber.setOption("rtsp_transport", "tcp"); // 使用TCP而不是UDP
            grabber.start();
            System.out.println("连接到RTSP流成功");
            Java2DFrameConverter converter = new Java2DFrameConverter();
            Frame frame;
            int a=0;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(byteArrayOutputStream, grabber.getImageWidth(), grabber.getImageHeight());
//			 recorder = new FFmpegFrameRecorder(new org.bytedeco.javacv.FrameRecorder.FrameRecorderAVIO(
//					 (org.bytedeco.javacv.FrameRecorder.FrameRecorderAVIOCallback) bufferCallback::call, null
//			 ), 1280, 720);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("flv");
            System.out.println("帧率"+grabber.getFrameRate());
            recorder.setFrameRate(grabber.getFrameRate());
            recorder.setGopSize(30); // 每30帧一个关键帧
            recorder.setVideoQuality(0); // 最好的质量
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("tune", "zerolatency");
            recorder.start();


            byte[] flvHeader = createFLVHeader();
            SendWebSocket(flvHeader);
            while ((frame = grabber.grab()) != null) {

                // 将Frame转换为JavaCV的Mat
                int width=frame.imageWidth;
                int height=frame.imageHeight;
                System.out.println(a+"成功获取帧宽度"+width+"高度:"+height);
                Mat opencvMat=bufferedImageToMat(   converter.getBufferedImage(frame));
                if(a<=500) {
                    // 录制帧 b释放才会保存
                    Frame processedFrame = converterToFrame(opencvMat);
                    //重置byteArrayOutputStream
                    // byteArrayOutputStream.reset();
                    //编码写入FLV
                    recorder.record(processedFrame);
                    // 强制刷新recorder，确保数据写入ByteArrayOutputStream
                    //	 recorder.flush();
                    // 获取编码后的数据
                    byte[] encodedData = byteArrayOutputStream.toByteArray();
                    if (encodedData.length > 0) {
                        SendWebSocket(encodedData);
                        byteArrayOutputStream.reset(); // 重置输出流
                    }
               //     Imgcodecs.imwrite("F:\\JAVAAI\\model\\test3\\test"+a+".jpg", opencvMat);
                    // saveVideo(opencvMat, frame, videoWriter, "F:\\JAVAAI\\model\\test3\\test1.mp4");
                }else{
                    break;
                }
                a++;


                // 显示帧（如果您想要显示视频）
                //   canvasFrame.showImage(frame);

            }

            grabber.stop();
            grabber.release();

            recorder.stop();
            recorder.release();
            //     canvasFrame.dispose();
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private static ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
    boolean isStreaming=false;
    FFmpegFrameGrabber grabber;
    FFmpegFrameRecorder  recorder;
    @Override
    public void sendUrlFLV() throws FFmpegFrameGrabber.Exception, FFmpegFrameRecorder.Exception {
        try{
            String rtspUrl="rtsp://admin:ch255899@192.168.0.200/Streaming/Channels/102";
//        String rtspUrl = "rtsp://[用户名]:[密码]@[IP地址]:[端口]/[码流类型]";
            grabber = new FFmpegFrameGrabber(rtspUrl);
            grabber.start();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            recorder = new FFmpegFrameRecorder(outputStream, grabber.getImageWidth(), grabber.getImageHeight());
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_FLV1);
            recorder.setFormat("flv");
            recorder.setFrameRate(30);
            recorder.setGopSize(30 * 2);
            recorder.setVideoOption("preset", "ultrafast");
            recorder.setVideoOption("tune", "zerolatency");
            recorder.start();
            isStreaming = true;
            byte[] flvHeader = {0x46, 0x4C, 0x56, 0x01, 0x05, 0x00, 0x00, 0x00, 0x09};
            SendWebSocket(flvHeader);
            Frame frame;
            int a=0;
            while (isStreaming && (frame = grabber.grab()) != null) {
                //    processFrame(frame);

                if(a<=500){
                    // 将Frame转换为JavaCV的Mat

                    int width=frame.imageWidth;
                    int height=frame.imageHeight;
                    log.info(a+"成功获取帧宽度"+width+"高度:"+height);
//                    buffer.clear();
                    recorder.record(frame);
                    byte[] flvTag = outputStream.toByteArray();
                    if (flvTag.length > 0) {
                        SendWebSocket (flvTag);
                        outputStream.reset();
                    }
                //    buffer.flip();
                    // 获取编码后的FLV数据
//                    if (buffer.remaining() > 0) {
//                        webSocket.broadcastFrame(buffer);
//                    }
                }else {
                    break;
                }
                a++;

            }
        }catch (Exception ex){

        }finally {
            stopStreaming();
        }


    }
    private void stopStreaming() {
        isStreaming = false;
        try {
            if (grabber != null) {
                grabber.stop();
                grabber.release();
            }
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
        } catch (FFmpegFrameGrabber.Exception | FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
    private static class CustomOutputStream extends java.io.OutputStream {
        private final ByteBuffer buffer;

        public CustomOutputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int b) {
            buffer.put((byte) b);
        }

        @Override
        public void write(byte[] bytes, int off, int len) {
            buffer.put(bytes, off, len);
        }
    }
    public void SendWebSocket(byte[] text ){
        webSocket.pushMessageByte(text);
    }

    private byte[] createFLVHeader() {
        byte[] header = new byte[13];
        header[0] = 'F';
        header[1] = 'L';
        header[2] = 'V';
        header[3] = 1;
        header[4] = 5; // 有视频和音频
        header[5] = 0;
        header[6] = 0;
        header[7] = 0;
        header[8] = 9;
        return header;
    }
}
