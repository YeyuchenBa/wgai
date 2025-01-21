package org.jeecg.modules.demo.train.controller;

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
import org.jeecg.modules.demo.train.entity.TabTrainLog;
import org.jeecg.modules.demo.train.entity.TabTrainResult;
import org.jeecg.modules.demo.train.service.ITabTrainLogService;

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
 * @Description: 训练日志
 * @Author: jeecg-boot
 * @Date:   2025-01-16
 * @Version: V1.0
 */
@Api(tags="训练日志")
@RestController
@RequestMapping("/train/tabTrainLog")
@Slf4j
public class TabTrainLogController extends JeecgController<TabTrainLog, ITabTrainLogService> {
	@Autowired
	private ITabTrainLogService tabTrainLogService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabTrainLog
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "训练日志-分页列表查询")
	@ApiOperation(value="训练日志-分页列表查询", notes="训练日志-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabTrainLog>> queryPageList(TabTrainLog tabTrainLog,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabTrainLog> queryWrapper = QueryGenerator.initQueryWrapper(tabTrainLog, req.getParameterMap());
		Page<TabTrainLog> page = new Page<TabTrainLog>(pageNo, pageSize);
		IPage<TabTrainLog> pageList = tabTrainLogService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabTrainLog
	 * @return
	 */
	@AutoLog(value = "训练日志-添加")
	@ApiOperation(value="训练日志-添加", notes="训练日志-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_log:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabTrainLog tabTrainLog) {
		tabTrainLogService.save(tabTrainLog);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabTrainLog
	 * @return
	 */
	@AutoLog(value = "训练日志-编辑")
	@ApiOperation(value="训练日志-编辑", notes="训练日志-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_log:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabTrainLog tabTrainLog) {
		tabTrainLogService.updateById(tabTrainLog);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "训练日志-通过id删除")
	@ApiOperation(value="训练日志-通过id删除", notes="训练日志-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_log:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabTrainLogService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "训练日志-批量删除")
	@ApiOperation(value="训练日志-批量删除", notes="训练日志-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_log:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabTrainLogService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "训练日志-通过id查询")
	@ApiOperation(value="训练日志-通过id查询", notes="训练日志-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabTrainLog> queryById(@RequestParam(name="id",required=true) String id) {
		TabTrainLog tabTrainLog = tabTrainLogService.getById(id);
		if(tabTrainLog==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabTrainLog);
	}
	 @ApiOperation(value="训练结果-通过model_id查询", notes="训练结果-通过model_id查询")
	 @GetMapping(value = "/queryByModelId")
	 public Result<TabTrainLog> queryByModelId(@RequestParam(name="id",required=true) String id) {
		 QueryWrapper<TabTrainLog> queryWrapper =new QueryWrapper<>();
		 queryWrapper.eq("model_id",id);
		 List<TabTrainLog> tabTrainResult = tabTrainLogService.list(queryWrapper);
		 if(tabTrainResult==null||tabTrainResult.size()<=0) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(tabTrainResult.get(0));
	 }
    /**
    * 导出excel
    *
    * @param request
    * @param tabTrainLog
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_train_log:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabTrainLog tabTrainLog) {
        return super.exportXls(request, tabTrainLog, TabTrainLog.class, "训练日志");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_train_log:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabTrainLog.class);
    }

}
