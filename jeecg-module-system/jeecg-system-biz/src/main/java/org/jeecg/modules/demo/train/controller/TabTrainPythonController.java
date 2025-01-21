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
import org.jeecg.modules.demo.train.entity.TabModelTry;
import org.jeecg.modules.demo.train.entity.TabTrainPython;
import org.jeecg.modules.demo.train.service.ITabModelTryService;
import org.jeecg.modules.demo.train.service.ITabTrainPythonService;

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
 * @Description: 训练脚本模板
 * @Author: WGAI
 * @Date:   2025-01-14
 * @Version: V1.0
 */
@Api(tags="训练脚本模板")
@RestController
@RequestMapping("/train/tabTrainPython")
@Slf4j
public class TabTrainPythonController extends JeecgController<TabTrainPython, ITabTrainPythonService> {
	@Autowired
	private ITabTrainPythonService tabTrainPythonService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabTrainPython
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "训练脚本模板-分页列表查询")
	@ApiOperation(value="训练脚本模板-分页列表查询", notes="训练脚本模板-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabTrainPython>> queryPageList(TabTrainPython tabTrainPython,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabTrainPython> queryWrapper = QueryGenerator.initQueryWrapper(tabTrainPython, req.getParameterMap());
		Page<TabTrainPython> page = new Page<TabTrainPython>(pageNo, pageSize);
		IPage<TabTrainPython> pageList = tabTrainPythonService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabTrainPython
	 * @return
	 */
	@AutoLog(value = "训练脚本模板-添加")
	@ApiOperation(value="训练脚本模板-添加", notes="训练脚本模板-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_python:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabTrainPython tabTrainPython) {
		tabTrainPythonService.save(tabTrainPython);
		return Result.OK("添加成功！");
	}

	 /***
	  * 执行脚本 训练模型
	  * @param id 模型id
	  * @return
	  */
	 @AutoLog(value = "训练脚本模板-训练模型")
	 @ApiOperation(value="训练脚本模板-训练模型", notes="训练脚本模板-训练模型")
	 //@RequiresPermissions("org.jeecg.modules.demo:tab_train_python:add")
	 @GetMapping(value = "/startPy")
	 public Result<String> add(@RequestParam(name="id",required=true) String id) {
		 tabTrainPythonService.startPy(id,null);
		 return Result.OK("添加成功！");
	 }
	 @Autowired
	 private ITabModelTryService tabModelTryService;

	 /***
	  * 单步执行脚本 训练模型
	  * @return
	  */
	 @AutoLog(value = "训练脚本模板-训练模型")
	 @ApiOperation(value="训练脚本模板-训练模型", notes="训练脚本模板-训练模型")
	 //@RequiresPermissions("org.jeecg.modules.demo:tab_train_python:add")
	 @GetMapping(value = "/startOnePy")
	 public Result<String> startOnePy(@RequestParam(name="sort",required=true) String sort) {

		TabModelTry modelTryList=tabModelTryService.list().get(0);
		log.info("当前取值{}--模型名称:{}",modelTryList.getId(),modelTryList.getModelName());
		tabTrainPythonService.startPy(modelTryList.getId(),sort);
		return Result.OK("添加成功！");
	 }
	/**
	 *  编辑
	 *
	 * @param tabTrainPython
	 * @return
	 */
	@AutoLog(value = "训练脚本模板-编辑")
	@ApiOperation(value="训练脚本模板-编辑", notes="训练脚本模板-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_python:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabTrainPython tabTrainPython) {
		tabTrainPythonService.updateById(tabTrainPython);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "训练脚本模板-通过id删除")
	@ApiOperation(value="训练脚本模板-通过id删除", notes="训练脚本模板-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_python:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabTrainPythonService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "训练脚本模板-批量删除")
	@ApiOperation(value="训练脚本模板-批量删除", notes="训练脚本模板-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_train_python:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabTrainPythonService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "训练脚本模板-通过id查询")
	@ApiOperation(value="训练脚本模板-通过id查询", notes="训练脚本模板-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabTrainPython> queryById(@RequestParam(name="id",required=true) String id) {
		TabTrainPython tabTrainPython = tabTrainPythonService.getById(id);
		if(tabTrainPython==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabTrainPython);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabTrainPython
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_train_python:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabTrainPython tabTrainPython) {
        return super.exportXls(request, tabTrainPython, TabTrainPython.class, "训练脚本模板");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_train_python:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabTrainPython.class);
    }

}
