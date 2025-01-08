package org.jeecg.modules.demo.audio.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.audio.entity.TabAuditSetting;
import org.jeecg.modules.demo.audio.service.ITabAuditSettingService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 语音配置
 * @Author: WGAI
 * @Date:   2025-01-07
 * @Version: V1.0
 */
@Api(tags="语音配置")
@RestController
@RequestMapping("/audio/tabAuditSetting")
@Slf4j
public class TabAuditSettingController extends JeecgController<TabAuditSetting, ITabAuditSettingService> {
	@Autowired
	private ITabAuditSettingService tabAuditSettingService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabAuditSetting
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "语音配置-分页列表查询")
	@ApiOperation(value="语音配置-分页列表查询", notes="语音配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabAuditSetting>> queryPageList(TabAuditSetting tabAuditSetting,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabAuditSetting> queryWrapper = QueryGenerator.initQueryWrapper(tabAuditSetting, req.getParameterMap());
		Page<TabAuditSetting> page = new Page<TabAuditSetting>(pageNo, pageSize);
		IPage<TabAuditSetting> pageList = tabAuditSettingService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabAuditSetting
	 * @return
	 */
	@AutoLog(value = "语音配置-添加")
	@ApiOperation(value="语音配置-添加", notes="语音配置-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_audit_setting:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabAuditSetting tabAuditSetting) {
		tabAuditSettingService.save(tabAuditSetting);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabAuditSetting
	 * @return
	 */
	@AutoLog(value = "语音配置-编辑")
	@ApiOperation(value="语音配置-编辑", notes="语音配置-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_audit_setting:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabAuditSetting tabAuditSetting) {
		tabAuditSettingService.updateById(tabAuditSetting);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "语音配置-通过id删除")
	@ApiOperation(value="语音配置-通过id删除", notes="语音配置-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_audit_setting:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabAuditSettingService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "语音配置-批量删除")
	@ApiOperation(value="语音配置-批量删除", notes="语音配置-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_audit_setting:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabAuditSettingService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "语音配置-通过id查询")
	@ApiOperation(value="语音配置-通过id查询", notes="语音配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabAuditSetting> queryById(@RequestParam(name="id",required=true) String id) {
		TabAuditSetting tabAuditSetting = tabAuditSettingService.getById(id);
		if(tabAuditSetting==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabAuditSetting);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabAuditSetting
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_audit_setting:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabAuditSetting tabAuditSetting) {
        return super.exportXls(request, tabAuditSetting, TabAuditSetting.class, "语音配置");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_audit_setting:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabAuditSetting.class);
    }

}
