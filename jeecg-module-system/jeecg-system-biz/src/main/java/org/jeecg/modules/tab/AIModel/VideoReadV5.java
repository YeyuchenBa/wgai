package org.jeecg.modules.tab.AIModel;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

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
        capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧

        Mat frame = new Mat();
        MapTime mapTime=new MapTime();
        int c=0;
        while (capture.read(frame)) {

            Boolean flag= (Boolean) redisTemplate.opsForValue().get(videoUrl+""+userId);
            if(!flag){
                capture.release();
                break;
            }
            long timestamp = (long) capture.get(Videoio.CAP_PROP_POS_MSEC);
            int StartTime=VideoSendReadCfg.StartTime;
            if(StartTime==0){
                VideoSendReadCfg.StartTime= (int) timestamp;
            }else if(timestamp<=StartTime){
                log.info("【按帧读取】跳出本次循环{}-当前跳跃帧{}",StartTime,timestamp);
                continue;
            }else{
                VideoSendReadCfg.StartTime= (int) timestamp;

                log.info("【按帧读取】赋值最大值{}-跳出本次循环{}-当前跳跃帧{}",StartTime-timestamp,StartTime,timestamp);
            }

            Integer timeStart= (Integer) redisTemplate.opsForValue().get(videoUrl+"time"+userId);
            if(timeStart!=null){
                if(timeStart>timestamp){
                    continue;
                }
            }

                Object[] array = VideoSendReadCfg.matlist.toArray();
                if(array.length>0){
                    int matlistSize=array.length;
                    mapTime= (MapTime) array[matlistSize-1];
                    log.warn("【读取最新帧】-当前时间{},队列时间{}",timestamp,mapTime.times);
                    if(mapTime.times<timestamp){
                        if(matlistSize==10){
                            VideoSendReadCfg.matlist.poll();
                        }
                        mapTime.setTimes((int) timestamp);
                        mapTime.setMat(frame);
                        try {
                       //     redisTemplate.opsForValue().set("test"+timestamp,timestamp);
                            log.error("【读取最新帧】-更替最新存放时间戳！！！->{}--{}",timestamp,VideoFrameReader.millisecondsToHours(timestamp));
                            VideoSendReadCfg.matlist.offer(mapTime);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else{
                   // redisTemplate.opsForValue().set("test"+timestamp,timestamp);
                    log.warn("【读取最新帧】-存放时间戳{}--{}",timestamp,VideoFrameReader.millisecondsToHours(timestamp));
                    mapTime.setTimes((int) timestamp);
                    mapTime.setMat(frame);
                    try {
                        VideoSendReadCfg.matlist.offer(mapTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                c++;
            log.warn("【读取读取帧】-当前时间戳2:{}--{}当前线程数22222:{}",timestamp,VideoFrameReader.millisecondsToHours(timestamp));
        }
        capture.release();
    }
}
