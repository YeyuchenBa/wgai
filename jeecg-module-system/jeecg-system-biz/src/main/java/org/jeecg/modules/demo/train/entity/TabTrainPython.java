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
 * @Description: 训练脚本模板
 * @Author: WGAI
 * @Date:   2025-01-14
 * @Version: V1.0
 */
@Data
@TableName("tab_train_python")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_train_python对象", description="训练脚本模板")
public class TabTrainPython implements Serializable {
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
	/**脚本名称*/
	@Excel(name = "脚本名称", width = 15)
    @ApiModelProperty(value = "脚本名称")
    private java.lang.String pyName;
	/**脚本文件*/
	@Excel(name = "脚本文件", width = 15)
    @ApiModelProperty(value = "脚本文件")
    private java.lang.String pyUrl;
	/**文件放置地址*/
	@Excel(name = "文件放置地址", width = 15)
    @ApiModelProperty(value = "文件放置地址")
    private java.lang.String pyPath;
	/**脚本备注*/
	@Excel(name = "脚本备注", width = 15)
    @ApiModelProperty(value = "脚本备注")
    private java.lang.String pyRemake;
	/**备用1*/
	@Excel(name = "备用1", width = 15)
    @ApiModelProperty(value = "备用1")
    private java.lang.String spareOne;
	/**备用2*/
	@Excel(name = "备用2", width = 15)
    @ApiModelProperty(value = "备用2")
    private java.lang.String spareTwo;
	/**备用3*/
	@Excel(name = "备用3", width = 15)
    @ApiModelProperty(value = "备用3")
    private java.lang.String spareThree;
	/**备用4*/
	@Excel(name = "备用4", width = 15)
    @ApiModelProperty(value = "备用4")
    private java.lang.String spareFour;
	/**脚本类型*/
	@Excel(name = "脚本类型", width = 15, dicCode = "py_type")
	@Dict(dicCode = "py_type")
    @ApiModelProperty(value = "脚本类型")
    private java.lang.String pyType;
	/**执行顺序*/
	@Excel(name = "执行顺序", width = 15)
    @ApiModelProperty(value = "执行顺序")
    private java.lang.Integer pySort;
}
