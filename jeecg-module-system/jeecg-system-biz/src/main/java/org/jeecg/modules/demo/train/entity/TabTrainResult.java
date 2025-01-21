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
 * @Description: 训练结果
 * @Author: jeecg-boot
 * @Date:   2025-01-16
 * @Version: V1.0
 */
@Data
@TableName("tab_train_result")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_train_result对象", description="训练结果")
public class TabTrainResult implements Serializable {
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
	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date startTime;
	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
	/**训练状态*/
	@Excel(name = "训练状态", width = 15)
    @ApiModelProperty(value = "训练状态")
    private java.lang.String trainState;
	/**评估类别*/
	@Excel(name = "评估类别", width = 15)
    @ApiModelProperty(value = "评估类别")
    private java.lang.String trainClass;
	/**测试图片数量*/
	@Excel(name = "测试图片数量", width = 15)
    @ApiModelProperty(value = "测试图片数量")
    private java.lang.String trainImages;
	/**精度*/
	@Excel(name = "精度", width = 15)
    @ApiModelProperty(value = "精度")
    private java.lang.String percision;
	/**召回率*/
	@Excel(name = "召回率", width = 15)
    @ApiModelProperty(value = "召回率")
    private java.lang.String recall;
	/**平均精度*/
	@Excel(name = "平均精度", width = 15)
    @ApiModelProperty(value = "平均精度")
    private java.lang.String map50;
	/**0.5_0.9下的平均精度*/
	@Excel(name = "0.5_0.9下的平均精度", width = 15)
    @ApiModelProperty(value = "0.5_0.9下的平均精度")
    private java.lang.String map5095;
	/**标签图片*/
	@Excel(name = "标签图片", width = 15)
    @ApiModelProperty(value = "标签图片")
    private java.lang.String labels;
	/**标签相关图*/
	@Excel(name = "标签相关图", width = 15)
    @ApiModelProperty(value = "标签相关图")
    private java.lang.String labelsCorrelogram;
	/**训练批次0*/
	@Excel(name = "训练批次0", width = 15)
    @ApiModelProperty(value = "训练批次0")
    private java.lang.String trainBatch0;
	/**训练批次1*/
	@Excel(name = "训练批次1", width = 15)
    @ApiModelProperty(value = "训练批次1")
    private java.lang.String trainBatch1;
	/**训练批次2*/
	@Excel(name = "训练批次2", width = 15)
    @ApiModelProperty(value = "训练批次2")
    private java.lang.String trainBatch2;

    @ApiModelProperty(value = "真实标签")
    private java.lang.String valBatch0Lables;

    @ApiModelProperty(value = "模型预测结果")
    private java.lang.String valBatch0Pred;

	/**PR曲线*/
	@Excel(name = "PR曲线", width = 15)
    @ApiModelProperty(value = "PR曲线")
    private java.lang.String prCurve;
	/**混淆矩阵*/
	@Excel(name = "混淆矩阵", width = 15)
    @ApiModelProperty(value = "混淆矩阵")
    private java.lang.String confusionMatrix;
	/**F1曲线*/
	@Excel(name = "F1曲线", width = 15)
    @ApiModelProperty(value = "F1曲线")
    private java.lang.String f1Curve;
	/**P曲线*/
	@Excel(name = "P曲线", width = 15)
    @ApiModelProperty(value = "P曲线")
    private java.lang.String ppCurve;
	/**R曲线*/
	@Excel(name = "R曲线", width = 15)
    @ApiModelProperty(value = "R曲线")
    private java.lang.String rrCurve;
	/**结果曲线*/
	@Excel(name = "结果曲线", width = 15)
    @ApiModelProperty(value = "结果曲线")
    private java.lang.String results;
	/**hyp文件*/
	@Excel(name = "hyp文件", width = 15)
    @ApiModelProperty(value = "hyp文件")
    private java.lang.String hypYaml;
	/**opt文件*/
	@Excel(name = "opt文件", width = 15)
    @ApiModelProperty(value = "opt文件")
    private java.lang.String optYaml;

    @ApiModelProperty(value = "onnx权重")
    private java.lang.String onnxWeight;

    @ApiModelProperty(value = "bestPt文件")
    private java.lang.String bestPt;
    @ApiModelProperty(value = "lastPt文件")
    private java.lang.String lastPt;
    @ApiModelProperty(value = "迭代次数")
    private java.lang.String epochs;
	/**模型名称*/
	@Excel(name = "模型名称", width = 15, dictTable = "tab_model_try", dicText = "model_name", dicCode = "id")
	@Dict(dictTable = "tab_model_try", dicText = "model_name", dicCode = "id")
    @ApiModelProperty(value = "模型名称")
    private java.lang.String modelId;
}
