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
import org.jeecg.modules.demo.easy.entity.TabEasyPic;
import org.jeecg.modules.demo.easy.service.ITabEasyPicService;

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
 * @Description: 训练图片
 * @Author: WGAI
 * @Date:   2024-12-17
 * @Version: V1.0
 */
@Api(tags="训练图片")
@RestController
@RequestMapping("/easy/tabEasyPic")
@Slf4j
public class TabEasyPicController extends JeecgController<TabEasyPic, ITabEasyPicService> {
	@Autowired
	private ITabEasyPicService tabEasyPicService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabEasyPic
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "训练图片-分页列表查询")
	@ApiOperation(value="训练图片-分页列表查询", notes="训练图片-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabEasyPic>> queryPageList(TabEasyPic tabEasyPic,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabEasyPic> queryWrapper = QueryGenerator.initQueryWrapper(tabEasyPic, req.getParameterMap());
		queryWrapper.orderByAsc("pic_name");
		Page<TabEasyPic> page = new Page<TabEasyPic>(pageNo, pageSize);
		IPage<TabEasyPic> pageList = tabEasyPicService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabEasyPic
	 * @return
	 */
	@AutoLog(value = "训练图片-添加")
	@ApiOperation(value="训练图片-添加", notes="训练图片-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_pic:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabEasyPic tabEasyPic) {
		tabEasyPicService.save(tabEasyPic);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabEasyPic
	 * @return
	 */
	@AutoLog(value = "训练图片-编辑")
	@ApiOperation(value="训练图片-编辑", notes="训练图片-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_pic:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabEasyPic tabEasyPic) {
		tabEasyPicService.updateById(tabEasyPic);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "训练图片-通过id删除")
	@ApiOperation(value="训练图片-通过id删除", notes="训练图片-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_pic:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabEasyPicService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "训练图片-批量删除")
	@ApiOperation(value="训练图片-批量删除", notes="训练图片-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_pic:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabEasyPicService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "训练图片-通过id查询")
	@ApiOperation(value="训练图片-通过id查询", notes="训练图片-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabEasyPic> queryById(@RequestParam(name="id",required=true) String id) {
		TabEasyPic tabEasyPic = tabEasyPicService.getById(id);
		if(tabEasyPic==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabEasyPic);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabEasyPic
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_easy_pic:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabEasyPic tabEasyPic) {
        return super.exportXls(request, tabEasyPic, TabEasyPic.class, "训练图片");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_easy_pic:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabEasyPic.class);
    }

}
