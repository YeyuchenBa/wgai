package org.jeecg.modules.demo.tab.controller;

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
import org.jeecg.modules.demo.tab.entity.TabAiModel;
import org.jeecg.modules.demo.tab.service.ITabAiModelService;

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
 * @Description: AI模型
 * @Author: jeecg-boot
 * @Date:   2024-03-13
 * @Version: V1.0
 */
@Api(tags="AI模型")
@RestController
@RequestMapping("/tab/tabAiModel")
@Slf4j
public class TabAiModelController extends JeecgController<TabAiModel, ITabAiModelService> {
	@Autowired
	private ITabAiModelService tabAiModelService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabAiModel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "AI模型-分页列表查询")
	@ApiOperation(value="AI模型-分页列表查询", notes="AI模型-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabAiModel>> queryPageList(TabAiModel tabAiModel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabAiModel> queryWrapper = QueryGenerator.initQueryWrapper(tabAiModel, req.getParameterMap());
		Page<TabAiModel> page = new Page<TabAiModel>(pageNo, pageSize);
		IPage<TabAiModel> pageList = tabAiModelService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabAiModel
	 * @return
	 */
	@AutoLog(value = "AI模型-添加")
	@ApiOperation(value="AI模型-添加", notes="AI模型-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_model:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabAiModel tabAiModel) {
		tabAiModelService.save(tabAiModel);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabAiModel
	 * @return
	 */
	@AutoLog(value = "AI模型-编辑")
	@ApiOperation(value="AI模型-编辑", notes="AI模型-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_model:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabAiModel tabAiModel) {
		tabAiModelService.updateById(tabAiModel);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "AI模型-通过id删除")
	@ApiOperation(value="AI模型-通过id删除", notes="AI模型-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_model:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabAiModelService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "AI模型-批量删除")
	@ApiOperation(value="AI模型-批量删除", notes="AI模型-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_model:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabAiModelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "AI模型-通过id查询")
	@ApiOperation(value="AI模型-通过id查询", notes="AI模型-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabAiModel> queryById(@RequestParam(name="id",required=true) String id) {
		TabAiModel tabAiModel = tabAiModelService.getById(id);
		if(tabAiModel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabAiModel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabAiModel
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_ai_model:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabAiModel tabAiModel) {
        return super.exportXls(request, tabAiModel, TabAiModel.class, "AI模型");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_ai_model:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabAiModel.class);
    }

}
