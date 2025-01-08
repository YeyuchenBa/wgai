package org.jeecg.modules.demo.easy.entity;

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
 * @Description: 训练任务
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Data
@TableName("tab_easy_config")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_easy_config对象", description="训练任务")
public class TabEasyConfig implements Serializable {
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
	/**任务名称*/
	@Excel(name = "任务名称", width = 15)
    @ApiModelProperty(value = "任务名称")
    private java.lang.String confName;
	/**训练种类*/
	@Excel(name = "训练种类", width = 15)
    @ApiModelProperty(value = "训练种类")
    private java.lang.Integer typeNum;
	/**训练物体大小px*/
	@Excel(name = "训练物体大小px", width = 15)
    @ApiModelProperty(value = "训练物体大小px")
    private java.lang.Integer boxSize;
	/**图片数量*/
	@Excel(name = "图片数量", width = 15)
    @ApiModelProperty(value = "图片数量")
    private java.lang.Integer picNum;
	/**可信阈值*/
	@Excel(name = "可信阈值", width = 15)
    @ApiModelProperty(value = "可信阈值")
    private java.lang.Double pth;
	/**背景图片*/
	@Excel(name = "背景图片", width = 15)
    @ApiModelProperty(value = "背景图片")
    private java.lang.String backUrl;
	/**是否训练日志*/
	@Excel(name = "是否训练日志", width = 15)
    @ApiModelProperty(value = "是否训练日志")
    private java.lang.String logStatic;
	/**任务状态*/
	@Excel(name = "任务状态", width = 15)
    @ApiModelProperty(value = "任务状态")
    @Dict(dicCode = "study_status")
    private java.lang.String status;
	/**绑定训练图片*/
	@Excel(name = "绑定训练图片", width = 15)
    @ApiModelProperty(value = "绑定训练图片")
    private java.lang.String picList;
	/**备用*/
	@Excel(name = "备用", width = 15)
    @ApiModelProperty(value = "备用")
    private java.lang.String spaceOne;
	/**备用2*/
	@Excel(name = "备用2", width = 15)
    @ApiModelProperty(value = "备用2")
    private java.lang.String spaceTwo;
	/**备用3*/
	@Excel(name = "备用3", width = 15)
    @ApiModelProperty(value = "备用3")
    private java.lang.String spaceThree;
	/**类别*/
	@Excel(name = "类别", width = 15)
    @ApiModelProperty(value = "类别")
    private java.lang.String picTypeId;

    public transient  String picTypeName;
}
