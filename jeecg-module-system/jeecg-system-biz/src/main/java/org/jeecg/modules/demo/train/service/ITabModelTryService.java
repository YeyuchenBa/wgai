package org.jeecg.modules.demo.train.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.train.entity.TabModelTry;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.train.util.picXml;

import java.util.List;

/**
 * @Description: 模型预训练
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
public interface ITabModelTryService extends IService<TabModelTry> {

    public Result<String> savePatch(TabModelTry tabModelTry);

    //保存标注内容
    public Result<String> saveMake(List<picXml> picXml);
}
