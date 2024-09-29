package org.jeecg.modules.tab.AIModel.push;

import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
        Boolean flagSJ=redisTemplate.hasKey(tabAiSubscription.getId()+"sj");
        if(flagSJ!=null&&flagSJ!=false){
            log.info("【间隔存在直接结束】{}",flagSJ);
            return;
        }
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(tabAiSubscription.getRemake());
        try {

            log.info("读取视频流了开始{}",tabAiSubscription.getRemake());
            // # FFmpeg 视频处理程序  github

            Java2DFrameConverter converter = new Java2DFrameConverter();
            grabber.setOption("preset", "ultrafast"); // 可以尝试不同的预设
            grabber.setOption("probesize", "32");
            grabber.setOption("analyzeduration", "0");
            grabber.setOption("threads", "2"); // 限制使用的线程数
            grabber.setFrameRate(15); // 设置为每秒15帧，适当调整
          //  grabber.setOption("hwaccel", "cuda"); // 使用 CUDA 硬件加速
            grabber.start();
            // mp4 avi
            // rtsp rtmp  识别内容
            //             输入输出
            //
         //   rtsp:admin:xxxip@xxxxx xxx
       //     VideoCapture capture = new VideoCapture(tabAiSubscription.getRemake());
//            if (!capture.isOpened()) {
//                System.out.println("Error: Could not open video stream.");
//                return;
//            }
            // 尝试调整缓冲区大小
 //           capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧

            Mat opencvMat=new Mat();
            while (true) {
                //判断时间间隔是否存在

                //获取是否需要循环
                Boolean flag= (Boolean) redisTemplate.opsForValue().get(tabAiSubscription.getId());
                if(flag==null||!flag){
                   break;
                }
                Frame frame = grabber.grabImage();
                opencvMat=bufferedImageToMat(converter.getBufferedImage(frame));
         //       capture.read(opencvMat);




                if(opencvMat!=null){
                    //读到了就释放内存等待下一次
                    try {
                        queue.put(opencvMat);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    //间隔秒设置秒
                    log.info("【间隔生成 已结束当前内容】");
                    redisTemplate.opsForValue().set(tabAiSubscription.getId()+"sj",true,Integer.parseInt(tabAiSubscription.getEventNumber()), TimeUnit.SECONDS); //设置执行3600内
                    break;
                }


            }
      //      capture.release();

        }catch (Exception ex){
            ex.printStackTrace();
            try {
                grabber.release();
            } catch (FFmpegFrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
            try {
                grabber.close();
                System.gc(); // 强制进行垃圾回收
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
        }finally {
            log.info("结束内容");
            try {
                grabber.stop();
                grabber.release();
            } catch (FFmpegFrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
            try {
                grabber.close();
                System.gc(); // 强制进行垃圾回收
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Mat getMatFrame() {
        return matFrame;
    }

    public void setMatFrame(Mat matFrame) {
        this.matFrame = matFrame;
    }
}
