package org.jeecg.modules.demo.tab.entity;

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
 * @Description: Ai事件订阅
 * @Author: jeecg-boot
 * @Date:   2024-04-08
 * @Version: V1.0
 */
@Data
@TableName("tab_ai_subscription")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_ai_subscription对象", description="Ai事件订阅")
public class TabAiSubscription implements Serializable {
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
	/**订阅类型*/
	@Excel(name = "订阅类型", width = 15)
    @ApiModelProperty(value = "订阅类型")
    private java.lang.String eventTypes;

    private transient String eventTypesName;
	/**订阅回调地址*/
	@Excel(name = "订阅回调地址", width = 15)
    @ApiModelProperty(value = "订阅回调地址")
    private java.lang.String eventUrl;
	/**同类型报警间隔*/
	@Excel(name = "同类型报警间隔", width = 15)
    @ApiModelProperty(value = "同类型报警间隔")
    private java.lang.String eventNumber;
	/**报警消息*/
	@Excel(name = "报警消息", width = 15)
    @ApiModelProperty(value = "报警消息")
    private java.lang.String eventInfo;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remake;

    @Dict(dicCode = "push_static")
    @ApiModelProperty(value = "状态")
    Integer pushStatic;

    String indexCode;
}
