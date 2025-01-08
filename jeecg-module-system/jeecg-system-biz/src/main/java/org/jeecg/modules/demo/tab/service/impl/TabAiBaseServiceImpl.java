package org.jeecg.modules.demo.tab.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.demo.tab.mapper.TabAiBaseMapper;
import org.jeecg.modules.demo.tab.service.ITabAiBaseService;
import org.jeecg.modules.tab.AIModel.VideoSendReadCfg;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Description: AI基础信息
 * @Author: WGAI
 * @Date:   2024-03-20
 * @Version: V1.0
 */
@Service
public class TabAiBaseServiceImpl extends ServiceImpl<TabAiBaseMapper, TabAiBase> implements ITabAiBaseService {
    @Resource
    RedisUtil redisUtil;
    @Override
    public void SendRedisBase() {
        List<TabAiBase> base=this.list();
        redisUtil.set("AIModelBase", JSONObject.toJSONString(base));
        redisUtil.expire("AIModelBase",(24*60*60*365*1000));
        VideoSendReadCfg.map=VideoSendReadCfg.getMap(base);
    }
}
