package org.jeecg.modules.demo.video.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.tab.entity.TabAiModelBund;
import org.jeecg.modules.demo.tab.service.ITabAiModelBundService;
import org.jeecg.modules.demo.video.entity.TabVideoUtil;
import org.jeecg.modules.demo.video.mapper.TabVideoUtilMapper;
import org.jeecg.modules.demo.video.service.ITabVideoUtilService;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.service.ITabAiModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;

/**
 * @Description: 区域入侵配置
 * @Author: jeecg-boot
 * @Date:   2024-08-06
 * @Version: V1.0
 */
@Slf4j
@Service
public class TabVideoUtilServiceImpl extends ServiceImpl<TabVideoUtilMapper, TabVideoUtil> implements ITabVideoUtilService {

    @Resource
    WebSocket webSocket;
    @Resource
    RedisUtil redisUtil;
    @Autowired
    ITabAiModelService iTabAiModelService;
    @Autowired
    ITabAiModelBundService iTabAiModelBundService;
    @Autowired
    public RedisTemplate redisTemplate;
    @Override
    public Result<?> startVideoUtil(TabVideoUtil tabVideoUtil,String path) throws Exception {
        //开启入侵
        redisUtil.set(tabVideoUtil.getId(),true);
        redisUtil.expire(tabVideoUtil.getId(),365*24*60);//开启一次一年
        //入侵内容
        redisUtil.set("WGAI_RQNR"+tabVideoUtil.getId(),tabVideoUtil.getVideoJson());
        redisUtil.expire("WGAI_RQNR"+tabVideoUtil.getId(),365*24*60);//开启一次一年

        if(StringUtils.isNotEmpty(tabVideoUtil.getSpareTwo())){//区域入侵内容默认只使用v5
            //获取模型内容
            TabAiModel tabAiModel1=iTabAiModelService.getById(tabVideoUtil.getSpareTwo());
            //获取视频内容
            TabAiModelBund tabAiModelBund=iTabAiModelBundService.getById(tabVideoUtil.getVideoId());
            //开辟子线程去执行推理计算  2核一个视频流
            AIModelYolo3 modelYolo3=new AIModelYolo3();
            String savePath=modelYolo3.SendVideoLocalhostYoloV5ThreadVideoUtil(tabVideoUtil,tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path,webSocket,redisUtil,redisTemplate);

        }else{
            log.error("[未绑定模型咱不处理]");
            return Result.error("未绑定模型咱不处理");
        }
        //区域入侵 /

        return null;
    }

    @Override
    public Result<?> endVideoUtil(TabVideoUtil tabVideoUtil) {
        redisUtil.set(tabVideoUtil.getId(),false);
        redisUtil.expire(tabVideoUtil.getId(),365*24*60);//开启一次一年
        return Result.OK("结束成功");
    }
}
