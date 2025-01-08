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
import org.jeecg.modules.demo.easy.entity.PicConfig;
import org.jeecg.modules.demo.easy.service.IPicConfigService;

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
 * @Description: 图片任务关联表
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Api(tags="图片任务关联表")
@RestController
@RequestMapping("/easy/picConfig")
@Slf4j
public class PicConfigController extends JeecgController<PicConfig, IPicConfigService> {
	@Autowired
	private IPicConfigService picConfigService;
	
	/**
	 * 分页列表查询
	 *
	 * @param picConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "图片任务关联表-分页列表查询")
	@ApiOperation(value="图片任务关联表-分页列表查询", notes="图片任务关联表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<PicConfig>> queryPageList(PicConfig picConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<PicConfig> queryWrapper = QueryGenerator.initQueryWrapper(picConfig, req.getParameterMap());
		Page<PicConfig> page = new Page<PicConfig>(pageNo, pageSize);
		IPage<PicConfig> pageList = picConfigService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param picConfig
	 * @return
	 */
	@AutoLog(value = "图片任务关联表-添加")
	@ApiOperation(value="图片任务关联表-添加", notes="图片任务关联表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:pic_config:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody PicConfig picConfig) {
		picConfigService.save(picConfig);

		return Result.OK("添加成功！");
	}

	 /**
	  *   添加
	  *
	  * @param picConfig
	  * @return
	  */
	 @AutoLog(value = "图片任务关联表-添加")
	 @ApiOperation(value="图片任务关联表-添加", notes="图片任务关联表-批量添加")
	 //@RequiresPermissions("org.jeecg.modules.demo:pic_config:add")
	 @PostMapping(value = "/addList")
	 public Result<String> addList(@RequestBody PicConfig picConfig) {

		 return picConfigService.saveList(picConfig);
	 }
	
	/**
	 *  编辑
	 *
	 * @param picConfig
	 * @return
	 */
	@AutoLog(value = "图片任务关联表-编辑")
	@ApiOperation(value="图片任务关联表-编辑", notes="图片任务关联表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:pic_config:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody PicConfig picConfig) {
		picConfigService.updateById(picConfig);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "图片任务关联表-通过id删除")
	@ApiOperation(value="图片任务关联表-通过id删除", notes="图片任务关联表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:pic_config:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		picConfigService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "图片任务关联表-批量删除")
	@ApiOperation(value="图片任务关联表-批量删除", notes="图片任务关联表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:pic_config:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.picConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "图片任务关联表-通过id查询")
	@ApiOperation(value="图片任务关联表-通过id查询", notes="图片任务关联表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<PicConfig> queryById(@RequestParam(name="id",required=true) String id) {
		PicConfig picConfig = picConfigService.getById(id);
		if(picConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(picConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param picConfig
    */
    //@RequiresPermissions("org.jeecg.modules.demo:pic_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, PicConfig picConfig) {
        return super.exportXls(request, picConfig, PicConfig.class, "图片任务关联表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("pic_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, PicConfig.class);
    }

}
