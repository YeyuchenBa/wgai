package org.jeecg.modules.demo.train.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.train.entity.TabModelTry;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 模型预训练
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
public interface ITabModelTryService extends IService<TabModelTry> {

    public Result<String> savePatch(TabModelTry tabModelTry);
}
