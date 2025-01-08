package org.jeecg.modules.demo.video.entity;

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
 * @Description: 区域入侵配置
 * @Author: WGAI
 * @Date:   2024-08-06
 * @Version: V1.0
 */
@Data
@TableName("tab_video_util")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_video_util对象", description="区域入侵配置")
public class TabVideoUtil implements Serializable {
    private static final long serialVersionUID = 1L;

	/**id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "id")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**原始尺寸*/
	@Excel(name = "原始尺寸", width = 15)
    @ApiModelProperty(value = "原始尺寸")
    private java.lang.String videoStart;
	/**原始X坐标*/
	@Excel(name = "原始X坐标", width = 15)
    @ApiModelProperty(value = "原始X坐标")
    private java.lang.String videoStartx;
	/**原始y坐标*/
	@Excel(name = "原始y坐标", width = 15)
    @ApiModelProperty(value = "原始y坐标")
    private java.lang.String videoStarty;
	/**结束坐标x*/
	@Excel(name = "结束坐标x", width = 15)
    @ApiModelProperty(value = "结束坐标x")
    private java.lang.String videoEndx;
	/**结束坐标y*/
	@Excel(name = "结束坐标y", width = 15)
    @ApiModelProperty(value = "结束坐标y")
    private java.lang.String videoEndy;
	/**其他内容*/
	@Excel(name = "其他内容", width = 15)
    @ApiModelProperty(value = "其他内容")
    private java.lang.String videoJson;
	/**实际尺寸*/
	@Excel(name = "实际尺寸", width = 15)
    @ApiModelProperty(value = "实际尺寸")
    private java.lang.String canvasStart;
	/**实际X坐标*/
	@Excel(name = "实际X坐标", width = 15)
    @ApiModelProperty(value = "实际X坐标")
    private java.lang.String canvasStartx;
	/**实际y坐标*/
	@Excel(name = "实际y坐标", width = 15)
    @ApiModelProperty(value = "实际y坐标")
    private java.lang.String canvasStarty;
	/**实际宽度*/
	@Excel(name = "实际宽度", width = 15)
    @ApiModelProperty(value = "实际宽度")
    private java.lang.String canvasWidth;
	/**实际高度*/
	@Excel(name = "实际高度", width = 15)
    @ApiModelProperty(value = "实际高度")
    private java.lang.String canvasHeight;
	/**其他内容*/
	@Excel(name = "其他内容", width = 15)
    @ApiModelProperty(value = "其他内容")
    private java.lang.String canvasJson;
	/**remerk*/
	@Excel(name = "remerk", width = 15)
    @ApiModelProperty(value = "remerk")
    private java.lang.String remerk;
	/**备注*/
	@Excel(name = "状态检测", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String spareOne;
	/**视频id*/
	@Excel(name = "视频id", width = 15)
    @ApiModelProperty(value = "视频id")
    @Dict(dictTable = "tab_ai_model_bund", dicCode = "id", dicText = "space_two")
    private java.lang.String videoId;
	/**视频名称*/
	@Excel(name = "视频名称", width = 15)
    @ApiModelProperty(value = "视频名称")
    private java.lang.String videoName;
	/**备注2*/
	@Excel(name = "使用模型", width = 15)
    @ApiModelProperty(value = "使用模型")
    @Dict(dictTable = "tab_ai_model", dicCode = "id", dicText = "ai_name")
    private java.lang.String spareTwo;
	/**备注3*/
	@Excel(name = "备注3", width = 15)
    @ApiModelProperty(value = "备注3")
    private java.lang.String spateThree;
}
