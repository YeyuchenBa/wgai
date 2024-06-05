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
import org.jeecg.modules.demo.chat.entity.TabChatType;
import org.jeecg.modules.demo.chat.entity.TabMessageList;
import org.jeecg.modules.demo.chat.entity.TabMessageType;
import org.jeecg.modules.demo.chat.service.ITabChatKeywordService;
import org.jeecg.modules.demo.chat.service.ITabChatTypeService;
import org.jeecg.modules.demo.chat.service.ITabMessageListService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.demo.chat.service.ITabMessageTypeService;
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
 * @Description: 语句列表
 * @Author: jeecg-boot
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Api(tags="语句列表")
@RestController
@RequestMapping("/chat/tabMessageList")
@Slf4j
public class TabMessageListController extends JeecgController<TabMessageList, ITabMessageListService> {
	@Autowired
	private ITabMessageListService tabMessageListService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabMessageList
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "语句列表-分页列表查询")
	@ApiOperation(value="语句列表-分页列表查询", notes="语句列表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabMessageList>> queryPageList(TabMessageList tabMessageList,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabMessageList> queryWrapper = QueryGenerator.initQueryWrapper(tabMessageList, req.getParameterMap());
		Page<TabMessageList> page = new Page<TabMessageList>(pageNo, pageSize);
		IPage<TabMessageList> pageList = tabMessageListService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	 @Autowired
	 private ITabMessageTypeService tabMessageTypeService;
	 @Autowired
	 private ITabChatTypeService tabChatTypeService;
	 @Autowired
	 private ITabChatKeywordService tabChatKeywordService;
	 /**
	 *   添加
	 *
	 * @param tabMessageList
	 * @return
	 */
	@AutoLog(value = "语句列表-添加")
	@ApiOperation(value="语句列表-添加", notes="语句列表-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_list:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabMessageList tabMessageList) {
		//语义分类
		QueryWrapper<TabMessageType> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("chat_type",tabMessageList.getMessageId());
		TabMessageType  messageType=tabMessageTypeService.getOne(queryWrapper);
		messageType.setMessageNumber(messageType.getMessageNumber()+1);
		tabMessageTypeService.updateById(messageType);
		//基础分类
		TabChatType  tabChatType=tabChatTypeService.getById(messageType.getChatType());
		tabChatType.setSendSum(tabChatType.getSendSum());
		tabChatTypeService.updateById(tabChatType);
		//添加语句
		tabMessageListService.save(tabMessageList);
		log.info("语句列表 id{}",tabMessageList.getId());
		//添加关键词
		TabChatKeyword keyword=new TabChatKeyword();
		keyword.setMessageId(tabMessageList.getId());
		keyword.setKeysword(tabMessageList.getMessageKey());
		keyword.setKeyTypeId(tabChatType.getId());
		log.info("关键词 id{}",tabMessageList.getId());
		tabChatKeywordService.save(keyword);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabMessageList
	 * @return
	 */
	@AutoLog(value = "语句列表-编辑")
	@ApiOperation(value="语句列表-编辑", notes="语句列表-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_list:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabMessageList tabMessageList) {
		tabMessageListService.updateById(tabMessageList);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "语句列表-通过id删除")
	@ApiOperation(value="语句列表-通过id删除", notes="语句列表-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_list:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabMessageListService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "语句列表-批量删除")
	@ApiOperation(value="语句列表-批量删除", notes="语句列表-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_list:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabMessageListService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "语句列表-通过id查询")
	@ApiOperation(value="语句列表-通过id查询", notes="语句列表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabMessageList> queryById(@RequestParam(name="id",required=true) String id) {
		TabMessageList tabMessageList = tabMessageListService.getById(id);
		if(tabMessageList==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabMessageList);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabMessageList
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_message_list:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabMessageList tabMessageList) {
        return super.exportXls(request, tabMessageList, TabMessageList.class, "语句列表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_message_list:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabMessageList.class);
    }

}
