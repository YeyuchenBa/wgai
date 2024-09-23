package org.jeecg.modules.tab.AIModel.push;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.BlockingQueue;

import static org.jeecg.modules.tab.AIModel.AIModelYolo3.bufferedImageToMat;

/**
 * @author wggg
 * @date 2024/9/19 13:49
 */
@Slf4j
public class ReadVideoImg  implements Runnable{

    Mat matFrame;
    private RedisTemplate redisTemplate;
    TabAiSubscription tabAiSubscription;
    BlockingQueue<Mat> queue;
    public ReadVideoImg(TabAiSubscription tabAiSubscription,RedisTemplate redisTemplate,BlockingQueue<Mat> queue){
        this.tabAiSubscription=tabAiSubscription;
        this.redisTemplate=redisTemplate;
        this.queue=queue;
    }
    @Override
    public void run() {
        try {

            log.info("读取视频流了开始{}",tabAiSubscription.getRemake());
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tabAiSubscription.getRemake());
            Java2DFrameConverter converter = new Java2DFrameConverter();
            grabber.start();
       //     VideoCapture capture = new VideoCapture(tabAiSubscription.getRemake());
//            if (!capture.isOpened()) {
//                System.out.println("Error: Could not open video stream.");
//                return;
//            }
            // 尝试调整缓冲区大小
 //           capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧

            Mat opencvMat=new Mat();
            while (true) {
                Boolean flag= (Boolean) redisTemplate.opsForValue().get(tabAiSubscription.getId());
                if(flag==null||!flag){
                   break;
                }
                Frame frame = grabber.grabImage();
                opencvMat=bufferedImageToMat(converter.getBufferedImage(frame));
         //       capture.read(opencvMat);


                try {
                    queue.put(opencvMat);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
      //      capture.release();
            grabber.release();
            grabber.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Mat getMatFrame() {
        return matFrame;
    }

    public void setMatFrame(Mat matFrame) {
        this.matFrame = matFrame;
    }
}
