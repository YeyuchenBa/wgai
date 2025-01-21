package org.jeecg.modules.demo.train.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 训练日志
 * @Author: jeecg-boot
 * @Date:   2025-01-16
 * @Version: V1.0
 */
@Data
@TableName("tab_train_log")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_train_log对象", description="训练日志")
public class TabTrainLog implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**模型名称*/
	@Excel(name = "模型名称", width = 15, dictTable = "tab_model_try", dicText = "model_name", dicCode = "id")
	@Dict(dictTable = "tab_model_try", dicText = "model_name", dicCode = "id")
    @ApiModelProperty(value = "模型名称")
    private java.lang.String modelId;
	/**日志内容*/
	@Excel(name = "日志内容", width = 15)
    @ApiModelProperty(value = "日志内容")
    private java.lang.String trainLog;
	/**结果id*/
	@Excel(name = "结果id", width = 15)
    @ApiModelProperty(value = "结果id")
    private java.lang.String resultId;

    /**
     * 模型保存文件地址
     */
    @ApiModelProperty(value = "模型保存文件地址")
    private java.lang.String cmdPath;
    /**
     * 模型保存结果
     */
    @ApiModelProperty(value = "模型保存结果")
    private java.lang.String cmdText;
}
