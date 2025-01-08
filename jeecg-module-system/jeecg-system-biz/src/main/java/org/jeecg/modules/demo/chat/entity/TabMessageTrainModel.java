package org.jeecg.modules.demo.chat.entity;

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
 * @Description: 语音训练模型
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Data
@TableName("tab_message_train_model")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_message_train_model对象", description="语音训练模型")
public class TabMessageTrainModel implements Serializable {
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
	/**模型标题*/
	@Excel(name = "模型标题", width = 15)
    @ApiModelProperty(value = "模型标题")
    private java.lang.String modelTitle;
	/**语义分类数*/
	@Excel(name = "语义分类数", width = 15)
    @ApiModelProperty(value = "语义分类数")
    private java.lang.String modelTypeNumber;
	/**语义分类嵌入维度*/
	@Excel(name = "语义分类嵌入维度", width = 15)
    @ApiModelProperty(value = "语义分类嵌入维度")
    private java.lang.String modelMessageInlay;
	/**问答词嵌入维度*/
	@Excel(name = "问答词嵌入维度", width = 15)
    @ApiModelProperty(value = "问答词嵌入维度")
    private java.lang.String modelBackInlay;
	/**用户语句最大长度*/
	@Excel(name = "用户语句最大长度", width = 15)
    @ApiModelProperty(value = "用户语句最大长度")
    private java.lang.String userMessageLenght;
	/**最大回复长度*/
	@Excel(name = "最大回复长度", width = 15)
    @ApiModelProperty(value = "最大回复长度")
    private java.lang.String modelMessageLenght;
	/**模型训练增强*/
	@Excel(name = "模型训练增强", width = 15)
    @ApiModelProperty(value = "模型训练增强")
    private java.lang.String modelEnhancement;
	/**正则抑制系数*/
	@Excel(name = "正则抑制系数", width = 15)
    @ApiModelProperty(value = "正则抑制系数")
    private java.lang.String inhibitionCoefficient;
	/**生成语义可信阈值*/
	@Excel(name = "生成语义可信阈值", width = 15)
    @ApiModelProperty(value = "生成语义可信阈值")
    private java.lang.Double modelThreshold;
	/**生成语义分类可信阈值*/
	@Excel(name = "生成语义分类可信阈值", width = 15)
    @ApiModelProperty(value = "生成语义分类可信阈值")
    private java.lang.String modelTypeThreshold;
	/**是否打印日志*/
	@Excel(name = "是否打印日志", width = 15)
    @ApiModelProperty(value = "是否打印日志")
    private java.lang.String debugStatus;
	/**日志详细信息*/
	@Excel(name = "日志详细信息", width = 15)
    @ApiModelProperty(value = "日志详细信息")
    private java.lang.String debugId;
	/**备注 */
	@Excel(name = "备注 ", width = 15)
    @ApiModelProperty(value = "备注 ")
    private java.lang.String remark;
	/**关键词敏感颗粒度*/
	@Excel(name = "关键词敏感颗粒度", width = 15)
    @ApiModelProperty(value = "关键词敏感颗粒度")
    private java.lang.String modelSensitivity;
	/**使用状态*/
	@Excel(name = "使用状态", width = 15)
    @ApiModelProperty(value = "使用状态")
    private java.lang.String modelStatus;
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
	/**备用4*/
	@Excel(name = "备用4", width = 15)
    @ApiModelProperty(value = "备用4")
    private java.lang.String spaceFour;
	/**备用5*/
	@Excel(name = "备用5", width = 15)
    @ApiModelProperty(value = "备用5")
    private java.lang.String spaceFive;
}
