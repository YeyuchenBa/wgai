package org.jeecg.modules.demo.tab.service;

import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.demo.tab.entity.TabAiHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.tab.entity.TabAiModelBund;
import org.jeecg.modules.tab.entity.TabAiModel;

/**
 * @Description: AI识别结果历史
 * @Author: jeecg-boot
 * @Date:   2024-03-13
 * @Version: V1.0
 */
public interface ITabAiHistoryService extends IService<TabAiHistory> {


    /**
     * 添加自动识别历史记录
     * @param
     */
    public int saveIdentify(TabAiModelBund tabAiModelBund,String path);

    /**
     * 关闭自动识别历史记录
     * @param
     */
    public int closedentify(TabAiModelBund tabAiModelBund,LoginUser sysUser);

    /***
     * 添加自动识别视频历史记录
     * @param tabAiModelBund
     * @param path
     * @return
     */
    public int saveIdentifyVideo(TabAiModelBund tabAiModelBund,String path);

    /***
     * 添加识别历史
     * @param tabAiModelBund
     * @param path
     * @return
     */
    public int saveIdentifyLocalVideo(TabAiModelBund tabAiModelBund,String path,String userId);

    /***
     * 多线程输出
     * @param tabAiModelBund
     * @param path
     * @param userId
     * @return
     */
    public int saveIdentifyLocalVideoThread(TabAiModelBund tabAiModelBund,String path,String userId);

}