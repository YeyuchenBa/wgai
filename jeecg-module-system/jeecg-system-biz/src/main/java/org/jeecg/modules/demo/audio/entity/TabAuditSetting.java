package org.jeecg.modules.demo.audio.entity;

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
 * @Description: 语音配置
 * @Author: WGAI
 * @Date:   2025-01-07
 * @Version: V1.0
 */
@Data
@TableName("tab_audit_setting")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_audit_setting对象", description="语音配置")
public class TabAuditSetting implements Serializable {
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
	/**是否使用*/
	@Excel(name = "是否使用", width = 15, dicCode = "run_state")
	@Dict(dicCode = "run_state")
    @ApiModelProperty(value = "是否使用")
    private java.lang.Integer isStart;
	/**热词*/
	@Excel(name = "热词", width = 15)
    @ApiModelProperty(value = "热词")
    private java.lang.String hotWord;
	/**encoder权重*/
	@Excel(name = "encoder权重", width = 15)
    @ApiModelProperty(value = "encoder权重")
    private java.lang.String encoderPath;
	/**decoder权重*/
	@Excel(name = "decoder权重", width = 15)
    @ApiModelProperty(value = "decoder权重")
    private java.lang.String decoderPath;
	/**joiner权重*/
	@Excel(name = "joiner权重", width = 15)
    @ApiModelProperty(value = "joiner权重")
    private java.lang.String joinerPath;
	/**token占词*/
	@Excel(name = "token占词", width = 15)
    @ApiModelProperty(value = "token占词")
    private java.lang.String tokenPath;
	/**识别词类型*/
	@Excel(name = "识别词类型", width = 15)
    @ApiModelProperty(value = "识别词类型")
    private java.lang.String modeLing;
	/**识别模式*/
	@Excel(name = "识别模式", width = 15)
    @ApiModelProperty(value = "识别模式")
    private java.lang.String decodingMethod;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remake;
}
