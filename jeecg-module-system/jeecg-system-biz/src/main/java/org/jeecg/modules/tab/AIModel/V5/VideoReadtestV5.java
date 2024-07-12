package org.jeecg.modules.tab.AIModel.V5;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.tab.AIModel.V5.MapTime;
import org.jeecg.modules.tab.AIModel.V5.VideoFrameReaderV5;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

/***
 * 读取状态
 */
@Slf4j
public class VideoReadtestV5 implements Runnable {

    private RedisTemplate redisTemplate;
    public String videoUrl;

    public String userId;
    public VideoReadtestV5(String videoUrl, RedisTemplate redisTemplate, String userId){
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
        capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧

        Mat frame = new Mat();
        MapTime mapTime=new MapTime();
        while (capture.read(frame)) {

            Boolean flag= (Boolean) redisTemplate.opsForValue().get(videoUrl+"V5"+userId);
            if(!flag){
                capture.release();
                break;
            }
            VideoCapture videoCapture2=new VideoCapture(videoUrl);
            videoCapture2.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧
            videoCapture2.read(frame);

            long       timestamp= (long) videoCapture2.get(Videoio.CAP_PROP_POS_MSEC);

            int StartTime= VideoSendReadCfgV5.StartTime;
            if(StartTime==0){
                log.info("【跳帧读取】跳出本次循环{}-当前跳跃帧{}",StartTime,timestamp);
                continue;
            }else if(timestamp<=StartTime){
                continue;
            }else{
                VideoSendReadCfgV5.StartTime= (int) timestamp;
                log.info("【跳帧读取】赋值最大值{}-跳出本次循环{}-当前跳跃帧{}",StartTime-timestamp,StartTime,timestamp);
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
                log.warn("【跳跃最新帧】-当前时间{},队列时间{}",timestamp,mapTime.times);
                if(mapTime.times<timestamp){
                    if(matlistSize==10){
                        VideoSendReadCfgV5.matlist.poll();
                    }
                    mapTime.setTimes((int) timestamp);
                    mapTime.setMat(frame);
                    try {
                        log.warn("【跳跃最新帧】-存放时间戳{}--{}",timestamp, VideoFrameReaderV5.millisecondsToHours(timestamp));
                        VideoSendReadCfgV5.matlist.offer(mapTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                log.warn("【跳跃最新帧】-"+StartTime+"存放时间戳{}--{}",timestamp,VideoFrameReaderV5.millisecondsToHours(timestamp));
                mapTime.setTimes((int) timestamp);
                mapTime.setMat(frame);
                try {
                    VideoSendReadCfgV5.matlist.offer(mapTime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            log.warn("【读取最新帧】-当前时间戳{}--{}当前线程数11111:{}",timestamp,VideoFrameReaderV5.millisecondsToHours(timestamp));
        }
        capture.release();
    }
}
