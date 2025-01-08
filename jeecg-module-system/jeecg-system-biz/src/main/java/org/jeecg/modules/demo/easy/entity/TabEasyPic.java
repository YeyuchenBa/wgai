package org.jeecg.modules.demo.easy.entity;

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
 * @Description: 训练图片
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
@Data
@TableName("tab_easy_pic")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="tab_easy_pic对象", description="训练图片")
public class TabEasyPic implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	//@TableId(type = IdType.ASSIGN_ID)
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
	/**图片类型*/
	@Excel(name = "图片类型", width = 15, dicCode = "pic_type")
	@Dict(dicCode = "pic_type")
    @ApiModelProperty(value = "图片类型")
    private java.lang.String picType;
	/**图片名称*/
	@Excel(name = "图片名称", width = 15)
    @ApiModelProperty(value = "图片名称")
    private java.lang.String picName;
	/**图片地址*/
	@Excel(name = "图片地址", width = 15)
    @ApiModelProperty(value = "图片地址")
    private java.lang.String picUrl;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remake;
	/**是否标注*/
	@Excel(name = "是否标注", width = 15)
    @ApiModelProperty(value = "是否标注")
    private java.lang.String markType;
	/**标注文件*/
	@Excel(name = "标注文件", width = 15)
    @ApiModelProperty(value = "标注文件")
    private java.lang.String markXml;
	/**标注标签*/
	@Excel(name = "标注标签", width = 15)
    @ApiModelProperty(value = "标注标签")
    private java.lang.String markTitle;
}
