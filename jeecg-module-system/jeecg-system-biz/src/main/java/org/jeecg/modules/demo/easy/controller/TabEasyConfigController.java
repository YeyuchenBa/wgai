package org.jeecg.modules.demo.easy.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.easy.entity.TabEasyConfig;
import org.jeecg.modules.demo.easy.entity.TabEasyPic;
import org.jeecg.modules.demo.easy.service.ITabEasyConfigService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictItemService;
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
 * @Description: 训练任务
 * @Author: jeecg-boot
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Api(tags="训练任务")
@RestController
@RequestMapping("/easy/tabEasyConfig")
@Slf4j
public class TabEasyConfigController extends JeecgController<TabEasyConfig, ITabEasyConfigService> {
	@Autowired
	private ITabEasyConfigService tabEasyConfigService;
	 @Autowired
	 ISysDictItemService iSysDictItemService;
	/**
	 * 分页列表查询
	 *
	 * @param tabEasyConfig
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "训练任务-分页列表查询")
	@ApiOperation(value="训练任务-分页列表查询", notes="训练任务-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabEasyConfig>> queryPageList(TabEasyConfig tabEasyConfig,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabEasyConfig> queryWrapper = QueryGenerator.initQueryWrapper(tabEasyConfig, req.getParameterMap());
		Page<TabEasyConfig> page = new Page<TabEasyConfig>(pageNo, pageSize);
		IPage<TabEasyConfig> pageList = tabEasyConfigService.page(page, queryWrapper);
		List<String> typeids = pageList.getRecords().stream().map(TabEasyConfig::getPicTypeId).collect(Collectors.toList());
		if(typeids!=null && typeids.size()>0){
			Map<String, String> res = new HashMap(typeids.size());
			for (String a:typeids) {
				if(a!=null){
					LambdaQueryWrapper<SysDictItem> query=new LambdaQueryWrapper<>();
					query.eq(SysDictItem::getDictId,"1773274288565043201");
					query.in(SysDictItem::getItemValue,a.split(","));
					List<SysDictItem> list= iSysDictItemService.list(query);
					res.put(a,list.stream().map(SysDictItem::getItemText).collect(Collectors.joining(",")));
				}
			}
			pageList.getRecords().forEach(item->{
				item.setPicTypeName(res.get(item.getPicTypeId()));
			});
		}
		log.info(pageList.toString());
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabEasyConfig
	 * @return
	 */
	@AutoLog(value = "训练任务-添加")
	@ApiOperation(value="训练任务-添加", notes="训练任务-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_config:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabEasyConfig tabEasyConfig) {
		tabEasyConfig.setStatus("0");
		tabEasyConfigService.save(tabEasyConfig);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabEasyConfig
	 * @return
	 */
	@AutoLog(value = "训练任务-编辑")
	@ApiOperation(value="训练任务-编辑", notes="训练任务-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_config:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabEasyConfig tabEasyConfig) {
		tabEasyConfigService.updateById(tabEasyConfig);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "训练任务-通过id删除")
	@ApiOperation(value="训练任务-通过id删除", notes="训练任务-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_config:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabEasyConfigService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "训练任务-批量删除")
	@ApiOperation(value="训练任务-批量删除", notes="训练任务-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_easy_config:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabEasyConfigService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "训练任务-通过id查询")
	@ApiOperation(value="训练任务-通过id查询", notes="训练任务-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabEasyConfig> queryById(@RequestParam(name="id",required=true) String id) {
		TabEasyConfig tabEasyConfig = tabEasyConfigService.getById(id);
		if(tabEasyConfig==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabEasyConfig);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabEasyConfig
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_easy_config:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabEasyConfig tabEasyConfig) {
        return super.exportXls(request, tabEasyConfig, TabEasyConfig.class, "训练任务");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_easy_config:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabEasyConfig.class);
    }

}
