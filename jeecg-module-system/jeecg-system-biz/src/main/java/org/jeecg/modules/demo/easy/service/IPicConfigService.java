package org.jeecg.modules.demo.easy.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.easy.entity.PicConfig;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 图片任务关联表
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
public interface IPicConfigService extends IService<PicConfig> {

    /**
     * 批量添加
     * @param picConfig
     * @return
     */
    Result<String> saveList(PicConfig picConfig);
}
