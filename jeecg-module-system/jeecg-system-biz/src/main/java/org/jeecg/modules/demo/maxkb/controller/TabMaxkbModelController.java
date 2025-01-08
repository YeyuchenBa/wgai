package org.jeecg.modules.demo.maxkb.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.maxkb.entity.TabMaxkbModel;
import org.jeecg.modules.demo.maxkb.service.ITabMaxkbModelService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.maxkb.util.maxkbutil;
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
 * @Description: 语言模型列表
 * @Author: WGAI
 * @Date:   2024-05-30
 * @Version: V1.0
 */
@Api(tags="语言模型列表")
@RestController
@RequestMapping("/maxkb/tabMaxkbModel")
@Slf4j
public class TabMaxkbModelController extends JeecgController<TabMaxkbModel, ITabMaxkbModelService> {
	@Autowired
	private ITabMaxkbModelService tabMaxkbModelService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabMaxkbModel
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "语言模型列表-分页列表查询")
	@ApiOperation(value="语言模型列表-分页列表查询", notes="语言模型列表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabMaxkbModel>> queryPageList(TabMaxkbModel tabMaxkbModel,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabMaxkbModel> queryWrapper = QueryGenerator.initQueryWrapper(tabMaxkbModel, req.getParameterMap());
		Page<TabMaxkbModel> page = new Page<TabMaxkbModel>(pageNo, pageSize);
		IPage<TabMaxkbModel> pageList = tabMaxkbModelService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabMaxkbModel
	 * @return
	 */
	@AutoLog(value = "语言模型列表-添加")
	@ApiOperation(value="语言模型列表-添加", notes="语言模型列表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_maxkb_model:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabMaxkbModel tabMaxkbModel) {
		tabMaxkbModelService.save(tabMaxkbModel);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabMaxkbModel
	 * @return
	 */
	@AutoLog(value = "语言模型列表-编辑")
	@ApiOperation(value="语言模型列表-编辑", notes="语言模型列表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_maxkb_model:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabMaxkbModel tabMaxkbModel) {
		tabMaxkbModelService.updateById(tabMaxkbModel);
		return Result.OK("编辑成功!");
	}


	 /**
	  *   连接模型更新模型状态
	  *
	  * @param
	  * @return
	  */
	 @AutoLog(value = "语言模型列表-连接模型更新模型状态")
	 @ApiOperation(value="语言模型列表-连接模型更新模型状态", notes="语言模型列表-连接模型更新模型状态")
	 @PostMapping(value = "/testConnect")
	 public Result<String> testConnect(@RequestBody TabMaxkbModel tabMaxkbModel) {
		 try {
			 String result=maxkbutil.getModelInfo(tabMaxkbModel.getStartUrl(),tabMaxkbModel.getApiKey());
			 JSONObject object=JSONObject.parseObject(result);
			 Integer code=object.getInteger("code");
			 if(code==200){
				 JSONObject data= (JSONObject) object.get("data");
				 tabMaxkbModel.setModelId(data.getString("id"));
				 tabMaxkbModel.setStatus("启用");
				 tabMaxkbModelService.updateById(tabMaxkbModel);
				 return Result.OK("更新模型成功");
			 }else{
				 return Result.error("更新模型失败请检查问题");
			 }
		 }catch (Exception ex){
			 ex.printStackTrace();
			 return Result.error("更新模型失败请检查问题");
		 }


	 }


	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "语言模型列表-通过id删除")
	@ApiOperation(value="语言模型列表-通过id删除", notes="语言模型列表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_maxkb_model:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabMaxkbModelService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "语言模型列表-批量删除")
	@ApiOperation(value="语言模型列表-批量删除", notes="语言模型列表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_maxkb_model:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabMaxkbModelService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "语言模型列表-通过id查询")
	@ApiOperation(value="语言模型列表-通过id查询", notes="语言模型列表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabMaxkbModel> queryById(@RequestParam(name="id",required=true) String id) {
		TabMaxkbModel tabMaxkbModel = tabMaxkbModelService.getById(id);
		if(tabMaxkbModel==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabMaxkbModel);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabMaxkbModel
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_maxkb_model:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabMaxkbModel tabMaxkbModel) {
        return super.exportXls(request, tabMaxkbModel, TabMaxkbModel.class, "语言模型列表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_maxkb_model:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabMaxkbModel.class);
    }

}
