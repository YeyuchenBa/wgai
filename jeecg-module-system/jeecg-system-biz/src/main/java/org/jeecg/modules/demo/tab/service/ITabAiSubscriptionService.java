package org.jeecg.modules.demo.tab.service;

import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: Ai事件订阅
 * @Author: jeecg-boot
 * @Date:   2024-04-08
 * @Version: V1.0
 */
public interface ITabAiSubscriptionService extends IService<TabAiSubscription> {

    /***
     * 推送订阅地址事件
     */
    public void insertRedisSubscription();

}
