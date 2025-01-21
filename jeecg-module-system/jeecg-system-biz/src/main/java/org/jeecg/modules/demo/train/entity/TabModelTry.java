package org.jeecg.modules.demo.train.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;
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
 * @Description: 模型预训练
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
@Data
@TableName("tab_model_try")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_model_try对象", description="模型预训练")
public class TabModelTry implements Serializable {
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
	@Excel(name = "模型名称", width = 15)
    @ApiModelProperty(value = "模型名称")
    private java.lang.String modelName;
	/**模型类型*/
	@Excel(name = "模型类型", width = 15)
    @ApiModelProperty(value = "模型类型")
    private java.lang.String modelType;
	/**图片数量*/
	@Excel(name = "图片数量", width = 15)
    @ApiModelProperty(value = "图片数量")
    private java.lang.String picNumber;
	/**标签文件*/
	@Excel(name = "标签文件", width = 15)
    @ApiModelProperty(value = "标签文件")
    private java.lang.String txtTitle;
    @ApiModelProperty(value = "标签内容")
    private java.lang.String txtInfo;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
    @ApiModelProperty(value = "图片地址")
    private java.lang.String picUrl;
	/**图片简称*/
	@Excel(name = "图片简称", width = 15)
    @ApiModelProperty(value = "图片简称")
    private java.lang.String picName;
	/**是否覆盖*/
	@Excel(name = "是否覆盖", width = 15)
    @ApiModelProperty(value = "是否覆盖")
    private java.lang.String isInsert;

    //是否修改图片内容
    private java.lang.String updatePic;
    //标注说
    @ApiModelProperty(value = "标注数量")
    private java.lang.String makeNumber;

    @ApiModelProperty(value = "图片文件大小")
    private Double fileSize;

    @ApiModelProperty(value = "模型介绍")
    private java.lang.String modelTitle;
    @ApiModelProperty(value = "模型图片")
    private java.lang.String modelPic;

    @ApiModelProperty(value = "模型文件pt")
    private java.lang.String modelPt;
    @ApiModelProperty(value = "模型文件onnx")
    private java.lang.String modelOnnx;

    @ApiModelProperty(value = "备注")
    private java.lang.String ramerk;

    @ApiModelProperty(value = "运行状态")
    private java.lang.Integer runState;

    @ApiModelProperty(value = "训练开始时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date runDateStart;
    @ApiModelProperty(value = "训练结束时间")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date runDateEnd;

    /***
     * 是否有训练结果  0 没有 1 有
     */
    @ApiModelProperty(value = "是否有训练结果")
    private java.lang.Integer onnxIsok;
}
