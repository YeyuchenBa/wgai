package org.jeecg.modules.demo.tab.entity;

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
 * @Description: AI识别结果历史
 * @Author: jeecg-boot
 * @Date:   2024-03-13
 * @Version: V1.0
 */
@Data
@TableName("tab_ai_history")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_ai_history对象", description="AI识别结果历史")
public class TabAiHistory implements Serializable {
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
	/**模型id*/
	@Excel(name = "模型id", width = 15)
    @ApiModelProperty(value = "模型id")
    private java.lang.String modelId;
	/**模型名称*/
	@Excel(name = "模型名称", width = 15)
    @ApiModelProperty(value = "模型名称")
    private java.lang.String modelName;
	/**绑定id*/
	@Excel(name = "绑定id", width = 15)
    @ApiModelProperty(value = "绑定id")
    private java.lang.String bundId;
	/**绑定名称*/
	@Excel(name = "绑定名称", width = 15)
    @ApiModelProperty(value = "绑定名称")
    private java.lang.String bundName;
	/**耗时*/
	@Excel(name = "耗时", width = 15)
    @ApiModelProperty(value = "耗时")
    private java.lang.String sendTime;
	/**输出图片*/
	@Excel(name = "输出图片", width = 15)
    @ApiModelProperty(value = "输出图片")
    private java.lang.String sendUrl;
	/**输出结果*/
	@Excel(name = "输出结果", width = 15)
    @ApiModelProperty(value = "输出结果")
    private java.lang.String sendMsg;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remake;
}
