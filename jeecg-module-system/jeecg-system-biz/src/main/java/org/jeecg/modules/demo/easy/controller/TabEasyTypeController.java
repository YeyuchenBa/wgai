package org.jeecg.modules.demo.easy.controller;

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
import org.jeecg.modules.demo.easy.entity.TabEasyType;
import org.jeecg.modules.demo.easy.service.ITabEasyTypeService;

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
 * @Description: 类别基础库
 * @Author: jeecg-boot
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Api(tags="类别基础库")
@RestController
@RequestMapping("/easy/tabEasyType")
@Slf4j
public class TabEasyTypeController extends JeecgController<TabEasyType, ITabEasyTypeService> {
	@Autowired
	private ITabEasyTypeService tabEasyTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabEasyType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "类别基础库-分页列表查询")
	@ApiOperation(value="类别基础库-分页列表查询", notes="类别基础库-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabEasyType>> queryPageList(TabEasyType tabEasyType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabEasyType> queryWrapper = QueryGenerator.initQueryWrapper(tabEasyType, req.getParameterMap());
		Page<TabEasyType> page = new Page<TabEasyType>(pageNo, pageSize);
		IPage<TabEasyType> pageList = tabEasyTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabEasyType
	 * @return
	 */
	@AutoLog(value = "类别基础库-添加")
	@ApiOperation(value="类别基础库-添加", notes="类别基础库-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_type:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabEasyType tabEasyType) {
		tabEasyTypeService.save(tabEasyType);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabEasyType
	 * @return
	 */
	@AutoLog(value = "类别基础库-编辑")
	@ApiOperation(value="类别基础库-编辑", notes="类别基础库-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_type:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabEasyType tabEasyType) {
		tabEasyTypeService.updateById(tabEasyType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "类别基础库-通过id删除")
	@ApiOperation(value="类别基础库-通过id删除", notes="类别基础库-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_type:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabEasyTypeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "类别基础库-批量删除")
	@ApiOperation(value="类别基础库-批量删除", notes="类别基础库-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_type:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabEasyTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "类别基础库-通过id查询")
	@ApiOperation(value="类别基础库-通过id查询", notes="类别基础库-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabEasyType> queryById(@RequestParam(name="id",required=true) String id) {
		TabEasyType tabEasyType = tabEasyTypeService.getById(id);
		if(tabEasyType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabEasyType);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabEasyType
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_easy_type:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabEasyType tabEasyType) {
        return super.exportXls(request, tabEasyType, TabEasyType.class, "类别基础库");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_easy_type:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabEasyType.class);
    }

}
