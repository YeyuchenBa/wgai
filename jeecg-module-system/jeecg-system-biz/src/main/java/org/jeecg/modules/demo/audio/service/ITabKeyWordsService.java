package org.jeecg.modules.demo.audio.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.demo.audio.entity.TabKeyWords;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 热词
 * @Author: WGAI
 * @Date:   2024-10-21
 * @Version: V1.0
 */
public interface ITabKeyWordsService extends IService<TabKeyWords> {


    public Result<List<TabKeyWords>> refreshKeyWord();
}
