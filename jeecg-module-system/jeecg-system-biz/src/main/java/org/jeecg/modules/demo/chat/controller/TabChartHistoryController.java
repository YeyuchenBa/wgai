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
import org.jeecg.modules.demo.chat.entity.TabChartHistory;
import org.jeecg.modules.demo.chat.service.ITabChartHistoryService;

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
 * @Description: 聊天历史记录
 * @Author: WGAI
 * @Date:   2024-03-28
 * @Version: V1.0
 */
@Api(tags="聊天历史记录")
@RestController
@RequestMapping("/chat/tabChartHistory")
@Slf4j
public class TabChartHistoryController extends JeecgController<TabChartHistory, ITabChartHistoryService> {
	@Autowired
	private ITabChartHistoryService tabChartHistoryService;
	
	/**
	 * 分页列表查询
	 *
	 * @param tabChartHistory
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "聊天历史记录-分页列表查询")
	@ApiOperation(value="聊天历史记录-分页列表查询", notes="聊天历史记录-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabChartHistory>> queryPageList(TabChartHistory tabChartHistory,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabChartHistory> queryWrapper = QueryGenerator.initQueryWrapper(tabChartHistory, req.getParameterMap());
		Page<TabChartHistory> page = new Page<TabChartHistory>(pageNo, pageSize);
		IPage<TabChartHistory> pageList = tabChartHistoryService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param tabChartHistory
	 * @return
	 */
	@AutoLog(value = "聊天历史记录-添加")
	@ApiOperation(value="聊天历史记录-添加", notes="聊天历史记录-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chart_history:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabChartHistory tabChartHistory) {
		tabChartHistoryService.save(tabChartHistory);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabChartHistory
	 * @return
	 */
	@AutoLog(value = "聊天历史记录-编辑")
	@ApiOperation(value="聊天历史记录-编辑", notes="聊天历史记录-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chart_history:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabChartHistory tabChartHistory) {
		tabChartHistoryService.updateById(tabChartHistory);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "聊天历史记录-通过id删除")
	@ApiOperation(value="聊天历史记录-通过id删除", notes="聊天历史记录-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chart_history:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabChartHistoryService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "聊天历史记录-批量删除")
	@ApiOperation(value="聊天历史记录-批量删除", notes="聊天历史记录-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_chart_history:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabChartHistoryService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "聊天历史记录-通过id查询")
	@ApiOperation(value="聊天历史记录-通过id查询", notes="聊天历史记录-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabChartHistory> queryById(@RequestParam(name="id",required=true) String id) {
		TabChartHistory tabChartHistory = tabChartHistoryService.getById(id);
		if(tabChartHistory==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabChartHistory);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabChartHistory
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_chart_history:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabChartHistory tabChartHistory) {
        return super.exportXls(request, tabChartHistory, TabChartHistory.class, "聊天历史记录");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_chart_history:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabChartHistory.class);
    }

}
