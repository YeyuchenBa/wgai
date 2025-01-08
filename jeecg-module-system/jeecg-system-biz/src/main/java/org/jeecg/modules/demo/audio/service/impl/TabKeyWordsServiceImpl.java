package org.jeecg.modules.demo.audio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.audio.entity.TabAuditSetting;
import org.jeecg.modules.demo.audio.entity.TabKeyWords;
import org.jeecg.modules.demo.audio.mapper.TabKeyWordsMapper;
import org.jeecg.modules.demo.audio.service.ITabAuditSettingService;
import org.jeecg.modules.demo.audio.service.ITabKeyWordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * @Description: 热词
 * @Author: WGAI
 * @Date:   2024-10-21
 * @Version: V1.0
 */
@Service
@Slf4j
public class TabKeyWordsServiceImpl extends ServiceImpl<TabKeyWordsMapper, TabKeyWords> implements ITabKeyWordsService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    private ITabAuditSettingService tabAuditSettingService;
    @Value(value = "${jeecg.path.upload}")
    private String uplopadPath;

    /***
     * 刷新缓存并保存内容到热词
     * @return
     */
    @Override
    public Result<List<TabKeyWords>> refreshKeyWord() {
        List<TabKeyWords> tabKeyWords = this.list();
        if(tabKeyWords.size()<=0) {
            return Result.error("当前未绑定热词");
        }
        redisTemplate.opsForValue().set("KeyWord",tabKeyWords);
        List<TabKeyWords> tabKeyWordsReids = (List<TabKeyWords>) redisTemplate.opsForValue().get("KeyWord");
        log.info("缓存信息List:{}",tabKeyWordsReids);
        QueryWrapper<TabKeyWords> queryWrapper=new QueryWrapper<>();
        queryWrapper.groupBy("hot_name");
        List<TabKeyWords> tabKeyWordsGroupby = this.list(queryWrapper);
        log.info("缓存信息Map:{}",tabKeyWordsGroupby);
        //写入热词文件
        QueryWrapper<TabAuditSetting> tabAuditSettingQueryWrapper=new QueryWrapper<>();
        tabAuditSettingQueryWrapper.eq("is_start",1); //当前使用中的
        List<TabAuditSetting> TabAuditSettingList=tabAuditSettingService.list(tabAuditSettingQueryWrapper);
        if(TabAuditSettingList.size()>0){
            TabAuditSetting tabAuditSetting=TabAuditSettingList.get(0);
            String howWord=uplopadPath+ File.separator+tabAuditSetting.getHotWord();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(howWord))) {
                // 写入内容
                for (TabKeyWords tabkey:tabKeyWordsGroupby) {
                    writer.write(tabkey.getHotName()+" :"+String.format("%.1f",Double.parseDouble(tabkey.getHotScore())));
                    writer.newLine(); // 换行
                }

              log.info("内容写入成功！{}",howWord);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return Result.OK(tabKeyWordsReids);
    }
}
