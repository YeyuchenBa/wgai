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
 * @Description: AI基础信息
 * @Author: WGAI
 * @Date:   2024-03-20
 * @Version: V1.0
 */
@Data
@TableName("tab_ai_base")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_ai_base对象", description="AI基础信息")
public class TabAiBase implements Serializable {
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
	/**英文标签名称*/
	@Excel(name = "英文标签名称", width = 15)
    @ApiModelProperty(value = "英文标签名称")
    private java.lang.String englishName;
	/**中文标签名称*/
	@Excel(name = "中文标签名称", width = 15)
    @ApiModelProperty(value = "中文标签名称")
    private java.lang.String chainName;
	/**RGB颜色代码*/
	@Excel(name = "RGB颜色代码", width = 15)
    @ApiModelProperty(value = "RGB颜色代码")
    private java.lang.String rgbColor;
	/**CSS颜色代码*/
	@Excel(name = "CSS颜色代码", width = 15)
    @ApiModelProperty(value = "CSS颜色代码")
    private java.lang.String cssColor;
	/**示例图片*/
	@Excel(name = "示例图片", width = 15)
    @ApiModelProperty(value = "示例图片")
    private java.lang.String sendUrl;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
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
