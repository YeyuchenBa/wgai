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
import org.jeecg.modules.demo.train.entity.TabModelTryOrg;
import org.jeecg.modules.demo.train.service.ITabModelTryOrgService;

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
 * @Description: 模型图片关联表
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
@Api(tags="模型图片关联表")
@RestController
@RequestMapping("/train/tabModelTryOrg")
@Slf4j
public class TabModelTryOrgController extends JeecgController<TabModelTryOrg, ITabModelTryOrgService> {
	@Autowired
	private ITabModelTryOrgService tabModelTryOrgService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabModelTryOrg
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "模型图片关联表-分页列表查询")
	@ApiOperation(value="模型图片关联表-分页列表查询", notes="模型图片关联表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabModelTryOrg>> queryPageList(TabModelTryOrg tabModelTryOrg,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabModelTryOrg> queryWrapper = QueryGenerator.initQueryWrapper(tabModelTryOrg, req.getParameterMap());
		Page<TabModelTryOrg> page = new Page<TabModelTryOrg>(pageNo, pageSize);
		IPage<TabModelTryOrg> pageList = tabModelTryOrgService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabModelTryOrg
	 * @return
	 */
	@AutoLog(value = "模型图片关联表-添加")
	@ApiOperation(value="模型图片关联表-添加", notes="模型图片关联表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try_org:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabModelTryOrg tabModelTryOrg) {
		tabModelTryOrgService.save(tabModelTryOrg);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabModelTryOrg
	 * @return
	 */
	@AutoLog(value = "模型图片关联表-编辑")
	@ApiOperation(value="模型图片关联表-编辑", notes="模型图片关联表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try_org:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabModelTryOrg tabModelTryOrg) {
		tabModelTryOrgService.updateById(tabModelTryOrg);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "模型图片关联表-通过id删除")
	@ApiOperation(value="模型图片关联表-通过id删除", notes="模型图片关联表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try_org:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabModelTryOrgService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "模型图片关联表-批量删除")
	@ApiOperation(value="模型图片关联表-批量删除", notes="模型图片关联表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try_org:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabModelTryOrgService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "模型图片关联表-通过id查询")
	@ApiOperation(value="模型图片关联表-通过id查询", notes="模型图片关联表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabModelTryOrg> queryById(@RequestParam(name="id",required=true) String id) {
		TabModelTryOrg tabModelTryOrg = tabModelTryOrgService.getById(id);
		if(tabModelTryOrg==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabModelTryOrg);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabModelTryOrg
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_model_try_org:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabModelTryOrg tabModelTryOrg) {
        return super.exportXls(request, tabModelTryOrg, TabModelTryOrg.class, "模型图片关联表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_model_try_org:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabModelTryOrg.class);
    }

}
