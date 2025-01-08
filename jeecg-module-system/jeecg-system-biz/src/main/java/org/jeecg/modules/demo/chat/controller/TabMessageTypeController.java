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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.jeecg.modules.demo.chat.service.ITabMessageTypeService;

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
 * @Description: 语义分类
 * @Author: WGAI
 * @Date:   2024-04-17
 * @Version: V1.0
 */
@Api(tags="语义分类")
@RestController
@RequestMapping("/chat/tabMessageType")
@Slf4j
public class TabMessageTypeController extends JeecgController<TabMessageType, ITabMessageTypeService> {
	@Autowired
	private ITabMessageTypeService tabMessageTypeService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabMessageType
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "语义分类-分页列表查询")
	@ApiOperation(value="语义分类-分页列表查询", notes="语义分类-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabMessageType>> queryPageList(TabMessageType tabMessageType,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabMessageType> queryWrapper = QueryGenerator.initQueryWrapper(tabMessageType, req.getParameterMap());
		Page<TabMessageType> page = new Page<TabMessageType>(pageNo, pageSize);
		IPage<TabMessageType> pageList = tabMessageTypeService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	 @Autowired
	 private ITabMessageListService tabMessageListService;
	 @Autowired
	 private ITabChatKeywordService tabChatKeywordService;
	 @Autowired
	 private ITabChatTypeService tabChatTypeService;

	 /**
	 *   添加
	 *
	 * @param tabMessageType
	 * @return
	 */
	@AutoLog(value = "语义分类-添加")
	@ApiOperation(value="语义分类-添加", notes="语义分类-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_type:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabMessageType tabMessageType) {
        //关键词
//		List<TabMessageList> list=tabMessageListService.list();
//		Integer info=0;
//		if(list!=null){
//			info=list.size();
//		}
		QueryWrapper<TabMessageType> queryWrapper=new QueryWrapper<>();
		queryWrapper.eq("chat_type",tabMessageType.getChatType());
		TabMessageType  messageType=tabMessageTypeService.getOne(queryWrapper);
		if(messageType==null){
			tabMessageType.setMessageNumber(0);
			tabMessageTypeService.save(tabMessageType);
		}else{
			return Result.error("已经存在！请勿重复添加");
		}





		//



		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabMessageType
	 * @return
	 */
	@AutoLog(value = "语义分类-编辑")
	@ApiOperation(value="语义分类-编辑", notes="语义分类-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_type:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabMessageType tabMessageType) {
		tabMessageTypeService.updateById(tabMessageType);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "语义分类-通过id删除")
	@ApiOperation(value="语义分类-通过id删除", notes="语义分类-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_type:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabMessageTypeService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "语义分类-批量删除")
	@ApiOperation(value="语义分类-批量删除", notes="语义分类-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_message_type:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabMessageTypeService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "语义分类-通过id查询")
	@ApiOperation(value="语义分类-通过id查询", notes="语义分类-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabMessageType> queryById(@RequestParam(name="id",required=true) String id) {
		TabMessageType tabMessageType = tabMessageTypeService.getById(id);
		if(tabMessageType==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabMessageType);
	}


	 /**
	  * 通过id查询
	  *
	  * @param id
	  * @return
	  */
	 //@AutoLog(value = "语义分类-通过id查询")
	 @ApiOperation(value="语义分类-通过id查询", notes="语义分类-通过id查询")
	 @GetMapping(value = "/queryBychatTypeId")
	 public Result<TabMessageType> queryBychatTypeId(@RequestParam(name="id",required=true) String id) {
		 LambdaQueryWrapper<TabMessageType> queryWrapper=new LambdaQueryWrapper<>();
		 queryWrapper.eq(TabMessageType::getChatType,id);
		 TabMessageType tabMessageType = tabMessageTypeService.getOne(queryWrapper);
		 if(tabMessageType==null) {
			 return Result.error("未找到对应数据");
		 }
		 return Result.OK(tabMessageType);
	 }


	 /**
    * 导出excel
    *
    * @param request
    * @param tabMessageType
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_message_type:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabMessageType tabMessageType) {
        return super.exportXls(request, tabMessageType, TabMessageType.class, "语义分类");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_message_type:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabMessageType.class);
    }

}
