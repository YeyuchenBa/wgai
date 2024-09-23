package org.jeecg.modules.tab.AIModel.push;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.opencv.core.Mat;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * @author wggg
 * @date 2024/9/19 15:35
 */
@Slf4j
public class AiImgResult implements Runnable {

    TabAiSubscription tabAiSubscription;

    Mat matFrame;
    private RedisTemplate redisTemplate;

    String uplpadPath;

    BlockingQueue<Mat> queue;
    List<TabAiModel> aiModel;

    public AiImgResult(List<TabAiModel> aiModel,TabAiSubscription tabAiSubscription, RedisTemplate redisTemplate, BlockingQueue<Mat> queue){
        this.tabAiSubscription=tabAiSubscription;
        this.redisTemplate=redisTemplate;
        this.queue=queue;
        this.aiModel=aiModel;
    }
    @Override
    public void run() {

        while (true){
            try {
                boolean flag= (boolean) redisTemplate.opsForValue().get(tabAiSubscription.getId());
                if(!flag){
                    break;
                }
                Mat mat=queue.take();
                for (TabAiModel tabaiModel: aiModel) {
                    switch (tabaiModel.getSpareOne()) {
                        case "1": //v3
                        {

                        }
                    }
                }
                log.info("开始自动识别");

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
