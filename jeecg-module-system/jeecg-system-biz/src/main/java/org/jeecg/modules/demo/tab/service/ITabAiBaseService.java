package org.jeecg.modules.demo.tab.service;

import org.jeecg.modules.demo.tab.entity.TabAiBase;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: AI基础信息
 * @Author: WGAI
 * @Date:   2024-03-20
 * @Version: V1.0
 */
public interface ITabAiBaseService extends IService<TabAiBase> {

    /***
     * 写入缓存
     */
    public void SendRedisBase();

}
