package org.jeecg.modules.demo.chat.controller;

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
import org.jeecg.modules.demo.chat.entity.TabMessageTrainLog;
import org.jeecg.modules.demo.chat.service.ITabMessageTrainLogService;

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
 * @Description: 语义训练日志
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Api(tags="语义训练日志")
@RestController
@RequestMapping("/chat/tabMessageTrainLog")
@Slf4j
public class TabMessageTrainLogController extends JeecgController<TabMessageTrainLog, ITabMessageTrainLogService> {
	@Autowired
	private ITabMessageTrainLogService tabMessageTrainLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabMessageTrainLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "语义训练日志-分页列表查询")
	@ApiOperation(value="语义训练日志-分页列表查询", notes="语义训练日志-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabMessageTrainLog>> queryPageList(TabMessageTrainLog tabMessageTrainLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabMessageTrainLog> queryWrapper = QueryGenerator.initQueryWrapper(tabMessageTrainLog, req.getParameterMap());
		Page<TabMessageTrainLog> page = new Page<TabMessageTrainLog>(pageNo, pageSize);
		IPage<TabMessageTrainLog> pageList = tabMessageTrainLogService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabMessageTrainLog
	 * @return
	 */
	@AutoLog(value = "语义训练日志-添加")
	@ApiOperation(value="语义训练日志-添加", notes="语义训练日志-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_train_log:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabMessageTrainLog tabMessageTrainLog) {
		tabMessageTrainLogService.save(tabMessageTrainLog);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabMessageTrainLog
	 * @return
	 */
	@AutoLog(value = "语义训练日志-编辑")
	@ApiOperation(value="语义训练日志-编辑", notes="语义训练日志-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_train_log:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabMessageTrainLog tabMessageTrainLog) {
		tabMessageTrainLogService.updateById(tabMessageTrainLog);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "语义训练日志-通过id删除")
	@ApiOperation(value="语义训练日志-通过id删除", notes="语义训练日志-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_train_log:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabMessageTrainLogService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "语义训练日志-批量删除")
	@ApiOperation(value="语义训练日志-批量删除", notes="语义训练日志-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_train_log:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabMessageTrainLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "语义训练日志-通过id查询")
	@ApiOperation(value="语义训练日志-通过id查询", notes="语义训练日志-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabMessageTrainLog> queryById(@RequestParam(name="id",required=true) String id) {
		TabMessageTrainLog tabMessageTrainLog = tabMessageTrainLogService.getById(id);
		if(tabMessageTrainLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabMessageTrainLog);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabMessageTrainLog
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_message_train_log:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabMessageTrainLog tabMessageTrainLog) {
        return super.exportXls(request, tabMessageTrainLog, TabMessageTrainLog.class, "语义训练日志");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_message_train_log:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabMessageTrainLog.class);
    }

}
