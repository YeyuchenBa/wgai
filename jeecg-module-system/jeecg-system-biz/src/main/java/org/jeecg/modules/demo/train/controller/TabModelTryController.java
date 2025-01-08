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
import org.jeecg.modules.demo.easy.entity.TabEasyPic;
import org.jeecg.modules.demo.easy.service.ITabEasyPicService;
import org.jeecg.modules.demo.train.entity.TabModelTry;
import org.jeecg.modules.demo.train.entity.TabModelTryOrg;
import org.jeecg.modules.demo.train.service.ITabModelTryOrgService;
import org.jeecg.modules.demo.train.service.ITabModelTryService;

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
 * @Description: 模型预训练
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
@Api(tags="模型预训练")
@RestController
@RequestMapping("/train/tabModelTry")
@Slf4j
public class TabModelTryController extends JeecgController<TabModelTry, ITabModelTryService> {
	@Autowired
	private ITabModelTryService tabModelTryService;
	 @Autowired
	 private ITabModelTryOrgService tabModelTryOrgService;

	 @Autowired
	 private ITabEasyPicService tabEasyPicService;
	/**
	 * 分页列表查询
	 *
	 * @param tabModelTry
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "模型预训练-分页列表查询")
	@ApiOperation(value="模型预训练-分页列表查询", notes="模型预训练-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabModelTry>> queryPageList(TabModelTry tabModelTry,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabModelTry> queryWrapper = QueryGenerator.initQueryWrapper(tabModelTry, req.getParameterMap());
		Page<TabModelTry> page = new Page<TabModelTry>(pageNo, pageSize);
		IPage<TabModelTry> pageList = tabModelTryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabModelTry
	 * @return
	 */
	@AutoLog(value = "模型预训练-添加")
	@ApiOperation(value="模型预训练-添加", notes="模型预训练-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabModelTry tabModelTry) {
		//tabModelTryService.save(tabModelTry);
		return tabModelTryService.savePatch( tabModelTry);
	}
	
	/**
	 *  编辑
	 *
	 * @param tabModelTry
	 * @return
	 */
	@AutoLog(value = "模型预训练-编辑")
	@ApiOperation(value="模型预训练-编辑", notes="模型预训练-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabModelTry tabModelTry) {

		// tabModelTryService.updateById(tabModelTry);
		return tabModelTryService.savePatch( tabModelTry);
	}
	 @ApiOperation(value="获取模型下所有图片列表", notes="获取模型下所有图片列表")
	 @GetMapping(value = "/listPic")
	 public Result<?> listPic(@RequestParam(name="id",required=true) String id) {

		 List<TabModelTryOrg> tabModelTryOrg = tabModelTryOrgService.list(new QueryWrapper<TabModelTryOrg>().eq("model_id",id));
		 if(tabModelTryOrg.size()<=0) {
			 return Result.error("未找到对应数据");
		 }
		 List<TabEasyPic> pic=tabEasyPicService.listByIds(tabModelTryOrg.stream().map(TabModelTryOrg::getPicId).collect(Collectors.toList()));
		 return  Result.ok(pic);
	 }
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "模型预训练-通过id删除")
	@ApiOperation(value="模型预训练-通过id删除", notes="模型预训练-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabModelTryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "模型预训练-批量删除")
	@ApiOperation(value="模型预训练-批量删除", notes="模型预训练-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_model_try:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabModelTryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "模型预训练-通过id查询")
	@ApiOperation(value="模型预训练-通过id查询", notes="模型预训练-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabModelTry> queryById(@RequestParam(name="id",required=true) String id) {
		TabModelTry tabModelTry = tabModelTryService.getById(id);
		if(tabModelTry==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabModelTry);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabModelTry
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_model_try:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabModelTry tabModelTry) {
        return super.exportXls(request, tabModelTry, TabModelTry.class, "模型预训练");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_model_try:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabModelTry.class);
    }

}
