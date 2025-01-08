package org.jeecg.modules.demo.tab.service.impl;

import org.jeecg.modules.demo.tab.entity.PushInfo;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.jeecg.modules.demo.tab.mapper.TabAiSubscriptionMapper;
import org.jeecg.modules.demo.tab.service.ITabAiSubscriptionService;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.mapper.TabAiModelMapper;
import org.jeecg.modules.tab.service.impl.TabAiModelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Ai事件订阅
 * @Author: WGAI
 * @Date:   2024-04-08
 * @Version: V1.0
 */
@Service
public class TabAiSubscriptionServiceImpl extends ServiceImpl<TabAiSubscriptionMapper, TabAiSubscription> implements ITabAiSubscriptionService {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    TabAiModelServiceImpl tabAiModelServiceImpl;
    @Override
    public void insertRedisSubscription() {
        List<TabAiSubscription> listSubscription=this.list();
        List<PushInfo> PushList=new ArrayList<>();
        for (TabAiSubscription aiSubscript:listSubscription) {
            if(aiSubscript.getPushStatic()==0){
                List<String> stringList= Arrays.asList(aiSubscript.getEventTypes().split(","));
                List<TabAiModel>  tabAiModels=tabAiModelServiceImpl.listByIds(stringList);
                PushInfo pushInfo=new PushInfo();
                pushInfo.setPushId(aiSubscript.getId());
                pushInfo.setPushUrl(aiSubscript.getEventUrl());
                pushInfo.setVideoURL(aiSubscript.getRemake());
                pushInfo.setTabAiModelList(tabAiModels);
                pushInfo.setTime(Integer.parseInt(aiSubscript.getEventNumber()));
                pushInfo.setIndexCode(aiSubscript.getIndexCode());
                PushList.add(pushInfo);
            }
        }
        redisTemplate.opsForValue().set("sendPush",PushList,365, TimeUnit.DAYS);
    }
}
