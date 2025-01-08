package org.jeecg.modules.demo.video.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.video.entity.TabVideoUtil;
import org.jeecg.modules.demo.video.service.ITabVideoUtilService;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 区域入侵配置
 * @Author: WGAI
 * @Date:   2024-08-06
 * @Version: V1.0
 */
@Api(tags="区域入侵配置")
@RestController
@RequestMapping("/video/tabVideoUtil")
@Slf4j
public class TabVideoUtilController extends JeecgController<TabVideoUtil, ITabVideoUtilService> {
	@Autowired
	private ITabVideoUtilService tabVideoUtilService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabVideoUtil
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "区域入侵配置-分页列表查询")
	@ApiOperation(value="区域入侵配置-分页列表查询", notes="区域入侵配置-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabVideoUtil>> queryPageList(TabVideoUtil tabVideoUtil,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabVideoUtil> queryWrapper = QueryGenerator.initQueryWrapper(tabVideoUtil, req.getParameterMap());
		Page<TabVideoUtil> page = new Page<TabVideoUtil>(pageNo, pageSize);
		IPage<TabVideoUtil> pageList = tabVideoUtilService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabVideoUtil
	 * @return
	 */
	@AutoLog(value = "区域入侵配置-添加")
	@ApiOperation(value="区域入侵配置-添加", notes="区域入侵配置-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_video_util:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabVideoUtil tabVideoUtil) {
		tabVideoUtilService.save(tabVideoUtil);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabVideoUtil
	 * @return
	 */
	@AutoLog(value = "区域入侵配置-编辑")
	@ApiOperation(value="区域入侵配置-编辑", notes="区域入侵配置-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_video_util:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabVideoUtil tabVideoUtil) {
		tabVideoUtilService.updateById(tabVideoUtil);
		return Result.OK("编辑成功!");
	}

	 @Value(value = "${jeecg.path.upload}")
	 private String uploadpath;
	 /**
	  *  开始区域检测
	  *
	  * @param tabVideoUtil
	  * @return
	  */
	 @AutoLog(value = "区域入侵配置-开启")
	 @ApiOperation(value="区域入侵配置-开启", notes="区域入侵配置-开启")
	 //@RequiresPermissions("org.jeecg.modules.demo:tab_video_util:edit")
	 @RequestMapping(value = "/startVideoUtil", method = {RequestMethod.POST})
	 public Result<String> startVideoUtil(@RequestBody TabVideoUtil tabVideoUtil) {
		 try {
			 CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
				 try {
					 tabVideoUtilService.startVideoUtil(tabVideoUtil, uploadpath);
				 } catch (Exception e) {
					 throw new RuntimeException(e);
				 }
			 }, Executors.newSingleThreadExecutor());
			 TabVideoUtil tabVideoUtil1=tabVideoUtilService.getById(tabVideoUtil.getId());
			 tabVideoUtil1.setSpareOne("1");
			 tabVideoUtilService.updateById(tabVideoUtil1);
		 } catch (Exception e) {
			 e.printStackTrace();
			 return Result.error("开启失败!");
		 }
		 return Result.OK("开启成功!");
	 }


	 /**
	  *  结束区域检测
	  *
	  * @param tabVideoUtil
	  * @return
	  */
	 @AutoLog(value = "区域入侵配置-结束")
	 @ApiOperation(value="区域入侵配置-结束", notes="区域入侵配置-结束")
	 //@RequiresPermissions("org.jeecg.modules.demo:tab_video_util:edit")
	 @RequestMapping(value = "/stopVideoUtil", method = {RequestMethod.POST})
	 public Result<String> stopVideoUtil(@RequestBody TabVideoUtil tabVideoUtil) {
		 tabVideoUtilService.endVideoUtil(tabVideoUtil);
		 TabVideoUtil tabVideoUtil1=tabVideoUtilService.getById(tabVideoUtil.getId());
		 tabVideoUtil1.setSpareOne("0");
		 tabVideoUtilService.updateById(tabVideoUtil1);
		 return Result.OK("结束成功!");
	 }
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "区域入侵配置-通过id删除")
	@ApiOperation(value="区域入侵配置-通过id删除", notes="区域入侵配置-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_video_util:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabVideoUtilService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "区域入侵配置-批量删除")
	@ApiOperation(value="区域入侵配置-批量删除", notes="区域入侵配置-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_video_util:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabVideoUtilService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "区域入侵配置-通过id查询")
	@ApiOperation(value="区域入侵配置-通过id查询", notes="区域入侵配置-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabVideoUtil> queryById(@RequestParam(name="id",required=true) String id) {
		TabVideoUtil tabVideoUtil = tabVideoUtilService.getById(id);
		if(tabVideoUtil==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabVideoUtil);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabVideoUtil
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_video_util:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabVideoUtil tabVideoUtil) {
        return super.exportXls(request, tabVideoUtil, TabVideoUtil.class, "区域入侵配置");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_video_util:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabVideoUtil.class);
    }

}
