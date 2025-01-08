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
 * @Description: 聊天历史记录
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Data
@TableName("tab_chart_history")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_chart_history对象", description="聊天历史记录")
public class TabChartHistory implements Serializable {
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
	/**用户*/
	@Excel(name = "用户", width = 15)
    @ApiModelProperty(value = "用户")
    private java.lang.String userName;
	/**用户消息*/
	@Excel(name = "用户消息", width = 15)
    @ApiModelProperty(value = "用户消息")
    private java.lang.String userMessage;
	/**AI回复*/
	@Excel(name = "AI回复", width = 15)
    @ApiModelProperty(value = "AI回复")
    private java.lang.String aiCallback;
	/**备用*/
	@Excel(name = "备用", width = 15)
    @ApiModelProperty(value = "备用")
    private java.lang.String spaceOne;
	/**备用*/
	@Excel(name = "备用", width = 15)
    @ApiModelProperty(value = "备用")
    private java.lang.String spaceTwo;
	/**备用*/
	@Excel(name = "备用", width = 15)
    @ApiModelProperty(value = "备用")
    private java.lang.String spaceThree;
	/**备用*/
	@Excel(name = "备用", width = 15)
    @ApiModelProperty(value = "备用")
    private java.lang.String spaceFour;
	/**备用*/
	@Excel(name = "备用", width = 15)
    @ApiModelProperty(value = "备用")
    private java.lang.String spaceFive;
}
