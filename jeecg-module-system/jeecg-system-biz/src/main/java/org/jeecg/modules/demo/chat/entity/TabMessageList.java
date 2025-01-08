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
 * @Description: 语句列表
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Data
@TableName("tab_message_list")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_message_list对象", description="语句回复列表")
public class TabMessageList implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.AUTO)
    @ApiModelProperty(value = "主键")
    private java.lang.Integer id;
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
	/**语义id*/
	@Excel(name = "语义id", width = 15)
    @ApiModelProperty(value = "语义id")
    private java.lang.String messageId;
	/**语义名称*/
	@Excel(name = "语义名称", width = 15)
    @ApiModelProperty(value = "语义名称")
    private java.lang.String messageName;
	/**关键词*/
	@Excel(name = "关键词", width = 15)
    @ApiModelProperty(value = "关键词")
    private java.lang.String messageKey;
	/**语句内容*/
	@Excel(name = "语句内容", width = 15)
    @ApiModelProperty(value = "语句内容")
    private java.lang.String messageInfo;
	/**备用1*/
	@Excel(name = "备用1", width = 15)
    @ApiModelProperty(value = "备用1")
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
