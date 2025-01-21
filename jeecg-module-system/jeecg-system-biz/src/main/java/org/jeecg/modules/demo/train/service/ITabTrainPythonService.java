package org.jeecg.modules.demo.train.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.train.entity.TabTrainPython;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 训练脚本模板
 * @Author: WGAI
 * @Date:   2025-01-14
 * @Version: V1.0
 */
public interface ITabTrainPythonService extends IService<TabTrainPython> {
    Result<String>  startPy(String id,String sort);
}
