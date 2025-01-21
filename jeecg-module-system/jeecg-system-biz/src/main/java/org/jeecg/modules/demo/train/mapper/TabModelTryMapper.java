package org.jeecg.modules.demo.train.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.demo.train.entity.TabModelTry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 模型预训练
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
public interface TabModelTryMapper extends BaseMapper<TabModelTry> {


    @Select("select count(1) from tab_easy_pic where model_id=#{modelId} and mark_type=#{markType}")
    Integer getMakeNum(String modelId, String markType);

}
