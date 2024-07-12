package org.jeecg.modules.tab.AIModel.V5;

import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.tab.AIModel.VideoFrameReader;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;

/***
 * 读取状态
 */
@Slf4j
public class VideoReadV5 implements Runnable {

    private RedisTemplate redisTemplate;
    public String videoUrl;

    public String userId;
    public VideoReadV5(String videoUrl, RedisTemplate redisTemplate, String userId){
        this.videoUrl=videoUrl;
        this.redisTemplate=redisTemplate;
        this.userId=userId;
    }
    @Override
    public void run() {
        VideoCapture capture = new VideoCapture(videoUrl);
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open video stream.");
            return;
        }
        // 尝试调整缓冲区大小
      //  capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧

        Mat frame = new Mat();
        MapTime mapTime=new MapTime();
        int c=0;
        while (capture.read(frame)) {

            Boolean flag= (Boolean) redisTemplate.opsForValue().get(videoUrl+"V5"+userId);
            log.info("开始推理{}读取视频内容",flag);
            if(!flag){
                capture.release();
                break;
            }
            long timestamp = (long) capture.get(Videoio.CAP_PROP_POS_MSEC);
            int StartTime= VideoSendReadCfgV5.StartTime;
            if(StartTime==0){
                VideoSendReadCfgV5.StartTime= (int) timestamp;
            }else if(timestamp<=StartTime){
                log.info("【按帧读取】跳出本次循环{}-当前跳跃帧{}",StartTime,timestamp);
                continue;
            }else{
                VideoSendReadCfgV5.StartTime= (int) timestamp;
                log.info("【按帧读取】赋值最大值{}-跳出本次循环{}-当前跳跃帧{}",StartTime-timestamp,StartTime,timestamp);
            }

            Integer timeStart= (Integer) redisTemplate.opsForValue().get(videoUrl+"timeV5"+userId);
            if(timeStart!=null){
                if(timeStart>timestamp){
                    continue;
                }
            }

            Object[] array = VideoSendReadCfgV5.matlist.toArray();
            if(array.length>0){
                int matlistSize=array.length;
                mapTime= (MapTime) array[matlistSize-1];
                log.warn("【读取最新帧】-当前时间{},队列时间{}",timestamp,mapTime.times);
                if(mapTime.times<timestamp){
                    if(matlistSize==10){
                        VideoSendReadCfgV5.matlist.poll();
                    }
                    mapTime.setTimes((int) timestamp);
                    mapTime.setMat(frame);
                    try {
                        //     redisTemplate.opsForValue().set("test"+timestamp,timestamp);
                        log.error("【读取最新帧】-更替最新存放时间戳！！！->{}--{}",timestamp, VideoFrameReaderV5.millisecondsToHours(timestamp));
                        VideoSendReadCfgV5.matlist.offer(mapTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                // redisTemplate.opsForValue().set("test"+timestamp,timestamp);
                log.warn("【读取最新帧】-存放时间戳{}--{}",timestamp,VideoFrameReaderV5.millisecondsToHours(timestamp));
                mapTime.setTimes((int) timestamp);
                mapTime.setMat(frame);
                try {
                    VideoSendReadCfgV5.matlist.offer(mapTime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            c++;
            log.warn("【读取读取帧】-当前时间戳2:{}--{}当前线程数22222:{}",timestamp,VideoFrameReaderV5.millisecondsToHours(timestamp));
        }
        capture.release();
    }

    public static void main(String[] args) {
        List<String> classes = Arrays.asList("person", "bicycle", "car", "motorcycle", "airplane", "bus", "train", "truck", "boat", "traffic light", "fire hydrant", "stop sign", "parking meter", "bench", "bird", "cat", "dog", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe", "backpack", "umbrella", "handbag", "tie", "suitcase", "frisbee", "skis", "snowboard", "sports ball", "kite", "baseball bat", "baseball glove", "skateboard", "surfboard", "tennis racket", "bottle", "wine glass", "cup", "fork", "knife", "spoon", "bowl", "banana", "apple", "sandwich", "orange", "broccoli", "carrot", "hot dog", "pizza", "donut", "cake", "chair", "couch", "potted plant", "bed", "dining table", "toilet", "tv", "laptop", "mouse", "remote", "keyboard", "cell phone", "microwave", "oven", "toaster", "sink", "refrigerator", "book", "clock", "vase", "scissors", "teddy bear", "hair drier", "toothbrush");
        for (int i = 0; i <classes.size() ; i++) {
            System.out.println(classes.get(i));
        }
    }
}
