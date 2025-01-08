package org.jeecg.modules.demo.chat.controller;

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
import org.jeecg.modules.demo.chat.entity.TabChatKeyword;
import org.jeecg.modules.demo.chat.service.ITabChatKeywordService;

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
 * @Description: 关键词模板
 * @Author: WGAI
 * @Date:   2024-04-18
 * @Version: V1.0
 */
@Api(tags="关键词模板")
@RestController
@RequestMapping("/chat/tabChatKeyword")
@Slf4j
public class TabChatKeywordController extends JeecgController<TabChatKeyword, ITabChatKeywordService> {
	@Autowired
	private ITabChatKeywordService tabChatKeywordService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabChatKeyword
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "关键词模板-分页列表查询")
	@ApiOperation(value="关键词模板-分页列表查询", notes="关键词模板-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabChatKeyword>> queryPageList(TabChatKeyword tabChatKeyword,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabChatKeyword> queryWrapper = QueryGenerator.initQueryWrapper(tabChatKeyword, req.getParameterMap());
		Page<TabChatKeyword> page = new Page<TabChatKeyword>(pageNo, pageSize);
		IPage<TabChatKeyword> pageList = tabChatKeywordService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabChatKeyword
	 * @return
	 */
	@AutoLog(value = "关键词模板-添加")
	@ApiOperation(value="关键词模板-添加", notes="关键词模板-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chat_keyword:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabChatKeyword tabChatKeyword) {
		tabChatKeywordService.save(tabChatKeyword);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabChatKeyword
	 * @return
	 */
	@AutoLog(value = "关键词模板-编辑")
	@ApiOperation(value="关键词模板-编辑", notes="关键词模板-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chat_keyword:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabChatKeyword tabChatKeyword) {
		tabChatKeywordService.updateById(tabChatKeyword);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "关键词模板-通过id删除")
	@ApiOperation(value="关键词模板-通过id删除", notes="关键词模板-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chat_keyword:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabChatKeywordService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "关键词模板-批量删除")
	@ApiOperation(value="关键词模板-批量删除", notes="关键词模板-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chat_keyword:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabChatKeywordService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "关键词模板-通过id查询")
	@ApiOperation(value="关键词模板-通过id查询", notes="关键词模板-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabChatKeyword> queryById(@RequestParam(name="id",required=true) String id) {
		TabChatKeyword tabChatKeyword = tabChatKeywordService.getById(id);
		if(tabChatKeyword==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabChatKeyword);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabChatKeyword
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_chat_keyword:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabChatKeyword tabChatKeyword) {
        return super.exportXls(request, tabChatKeyword, TabChatKeyword.class, "关键词模板");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_chat_keyword:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabChatKeyword.class);
    }

}
