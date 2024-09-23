package org.jeecg.modules.tab.AIModel.push;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.BlockingQueue;

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

        VideoCapture capture = new VideoCapture(tabAiSubscription.getEventUrl());
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open video stream.");
            return;
        }
        // 尝试调整缓冲区大小
        capture.set(Videoio.CAP_PROP_BUFFERSIZE, 1); // 设置缓冲区大小为1帧
        Mat frame = new Mat();
        while (capture.read(frame)) {
            Boolean flag= (Boolean) redisTemplate.opsForValue().get(tabAiSubscription.getId());
            if(!flag){
               break;
            }
            setMatFrame(frame);
            try {
                queue.put(frame);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        capture.release();

    }

    public Mat getMatFrame() {
        return matFrame;
    }

    public void setMatFrame(Mat matFrame) {
        this.matFrame = matFrame;
    }
}
