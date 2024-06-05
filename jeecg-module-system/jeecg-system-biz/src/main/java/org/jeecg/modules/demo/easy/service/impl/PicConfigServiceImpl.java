package org.jeecg.modules.demo.easy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.easy.entity.PicConfig;
import org.jeecg.modules.demo.easy.entity.TabEasyConfig;
import org.jeecg.modules.demo.easy.mapper.PicConfigMapper;
import org.jeecg.modules.demo.easy.mapper.TabEasyConfigMapper;
import org.jeecg.modules.demo.easy.mapper.TabEasyPicMapper;
import org.jeecg.modules.demo.easy.service.IPicConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 图片任务关联表
 * @Author: jeecg-boot
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Service
@Slf4j
public class PicConfigServiceImpl extends ServiceImpl<PicConfigMapper, PicConfig> implements IPicConfigService {

    @Autowired
    PicConfigMapper configMapper;
    @Autowired
    TabEasyConfigMapper tabEasyConfigMapper;
    @Autowired
    TabEasyPicMapper tabEasyPicMapper;

    @Override
    public Result<String> saveList(PicConfig picConfig) {

        try {
            List<String> list=picConfig.getList();
            List<PicConfig> piclist=new ArrayList<>();
            for (String str:list) {
                PicConfig pic=new PicConfig();
                pic.setConfigId(picConfig.getConfigId());
                pic.setPicId(str);
                piclist.add(pic);
            }
            this.saveBatch(piclist);
        }catch (Exception ex){
                log.error("添加错误");
                return Result.error("添加异常！");
        }

        return Result.ok("添加成功！");
    }
}
