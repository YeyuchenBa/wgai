package org.jeecg.modules.demo.maxkb.entity;

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
 * @Description: 语言模型列表
 * @Author: jeecg-boot
 * @Date:   2024-05-30
 * @Version: V1.0
 */
@Data
@TableName("tab_maxkb_model")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_maxkb_model对象", description="语言模型列表")
public class TabMaxkbModel implements Serializable {
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
	/**名字*/
	@Excel(name = "名字", width = 15)
    @ApiModelProperty(value = "名字")
    private java.lang.String name;
	/**模型id*/
	@Excel(name = "模型id", width = 15)
    @ApiModelProperty(value = "模型id")
    private java.lang.String modelId;
	/**模型状态*/
	@Excel(name = "模型状态", width = 15)
    @ApiModelProperty(value = "模型状态")
    private java.lang.String status;
	/**模型key*/
	@Excel(name = "模型key", width = 15)
    @ApiModelProperty(value = "模型key")
    private java.lang.String apiKey;
	/**模型嵌入访问*/
	@Excel(name = "模型嵌入访问", width = 15)
    @ApiModelProperty(value = "模型嵌入访问")
    private java.lang.String apiUrl;
	/**模型浮框访问*/
	@Excel(name = "模型浮框访问", width = 15)
    @ApiModelProperty(value = "模型浮框访问")
    private java.lang.String apiJs;
	/**原始url*/
	@Excel(name = "原始url", width = 15)
    @ApiModelProperty(value = "原始url")
    private java.lang.String startUrl;
}
