package org.jeecg.modules.tab.AIModel.V5Util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.video.entity.TabVideoUtil;
import org.jeecg.modules.tab.AIModel.V5.MapTime;
import org.jeecg.modules.tab.AIModel.V5.VideoFrameReaderV5;
import org.jeecg.modules.tab.AIModel.V5.VideoSendReadCfgV5;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

/***
 * 读取状态
 */
@Slf4j
public class VideoReadtestV5Util implements Runnable {

    private RedisTemplate redisTemplate;
    public String videoUrl;

    public String userId;
    TabVideoUtil tabVideoUtil;
    public VideoReadtestV5Util(TabVideoUtil tabVideoUtil,String videoUrl, RedisTemplate redisTemplate){
        this.videoUrl=videoUrl;
        this.redisTemplate=redisTemplate;
        this.tabVideoUtil=tabVideoUtil;
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
        MapTimeUtil mapTime=new MapTimeUtil();
        while (capture.read(frame)) {

            Boolean flag= (Boolean) redisTemplate.opsForValue().get(tabVideoUtil.getId());
            if(!flag){
                capture.release();
                break;
            }
            VideoCapture videoCapture2=new VideoCapture(videoUrl);
            videoCapture2.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧
            videoCapture2.read(frame);

            long       timestamp= (long) videoCapture2.get(Videoio.CAP_PROP_POS_MSEC);

            int StartTime= VideoSendReadCfgV5Util.StartTime;
            if(StartTime==0){
                log.info("【跳帧读取】跳出本次循环{}-当前跳跃帧{}",StartTime,timestamp);
                continue;
            }else if(timestamp<=StartTime){
                continue;
            }else{
                VideoSendReadCfgV5Util.StartTime= (int) timestamp;
                log.info("【跳帧读取】赋值最大值{}-跳出本次循环{}-当前跳跃帧{}",StartTime-timestamp,StartTime,timestamp);
            }


            Object[] array = VideoSendReadCfgV5Util.matlist.toArray();
            if(array.length>0){
                int matlistSize=array.length;
                mapTime= (MapTimeUtil) array[matlistSize-1];
                log.warn("【跳跃最新帧】-当前时间{},队列时间{}",timestamp,mapTime.times);
                if(mapTime.times<timestamp){
                    if(matlistSize==10){
                        VideoSendReadCfgV5Util.matlist.poll();
                    }
                    mapTime.setTimes((int) timestamp);
                    mapTime.setMat(frame);
                    try {
                        log.warn("【跳跃最新帧】-存放时间戳{}--{}",timestamp, VideoFrameReaderV5.millisecondsToHours(timestamp));
                        VideoSendReadCfgV5Util.matlist.offer(mapTime);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }else{
                log.warn("【跳跃最新帧】-"+StartTime+"存放时间戳{}--{}",timestamp,VideoFrameReaderV5.millisecondsToHours(timestamp));
                mapTime.setTimes((int) timestamp);
                mapTime.setMat(frame);
                try {
                    VideoSendReadCfgV5Util.matlist.offer(mapTime);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            log.warn("【读取最新帧】-当前时间戳{}--{}当前线程数11111:{}",timestamp,VideoFrameReaderV5.millisecondsToHours(timestamp));
        }
        capture.release();
    }
}
