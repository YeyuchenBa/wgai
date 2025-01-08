package org.jeecg.modules.demo.audio.controller;

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
import org.jeecg.modules.demo.audio.entity.TabKeyWords;
import org.jeecg.modules.demo.audio.service.ITabKeyWordsService;

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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

 /**
 * @Description: 热词
 * @Author: WGAI
 * @Date:   2024-10-21
 * @Version: V1.0
 */
@Api(tags="热词")
@RestController
@RequestMapping("/audio/tabKeyWords")
@Slf4j
public class TabKeyWordsController extends JeecgController<TabKeyWords, ITabKeyWordsService> {
	@Autowired
	private ITabKeyWordsService tabKeyWordsService;
	 @Autowired
	 RedisTemplate redisTemplate;
	/**
	 * 分页列表查询
	 *
	 * @param tabKeyWords
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "热词-分页列表查询")
	@ApiOperation(value="热词-分页列表查询", notes="热词-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabKeyWords>> queryPageList(TabKeyWords tabKeyWords,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabKeyWords> queryWrapper = QueryGenerator.initQueryWrapper(tabKeyWords, req.getParameterMap());
		Page<TabKeyWords> page = new Page<TabKeyWords>(pageNo, pageSize);
		IPage<TabKeyWords> pageList = tabKeyWordsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param tabKeyWords
	 * @return
	 */
	@AutoLog(value = "热词-添加")
	@ApiOperation(value="热词-添加", notes="热词-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_key_words:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabKeyWords tabKeyWords) {
		tabKeyWordsService.save(tabKeyWords);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param tabKeyWords
	 * @return
	 */
	@AutoLog(value = "热词-编辑")
	@ApiOperation(value="热词-编辑", notes="热词-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_key_words:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabKeyWords tabKeyWords) {
		tabKeyWordsService.updateById(tabKeyWords);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "热词-通过id删除")
	@ApiOperation(value="热词-通过id删除", notes="热词-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_key_words:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabKeyWordsService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "热词-批量删除")
	@ApiOperation(value="热词-批量删除", notes="热词-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_key_words:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabKeyWordsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "热词-通过id查询")
	@ApiOperation(value="热词-通过id查询", notes="热词-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabKeyWords> queryById(@RequestParam(name="id",required=true) String id) {
		TabKeyWords tabKeyWords = tabKeyWordsService.getById(id);
		if(tabKeyWords==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabKeyWords);
	}

	 @ApiOperation(value="热词-刷新缓存", notes="热词-刷新缓存")
	 @GetMapping(value = "/refreshKeyWord")
	 public Result<List<TabKeyWords>> refreshKeyWord(@RequestParam(name="id",required=true) String id) {

		 return tabKeyWordsService.refreshKeyWord();
	 }

    /**
    * 导出excel
    *
    * @param request
    * @param tabKeyWords
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_key_words:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabKeyWords tabKeyWords) {
        return super.exportXls(request, tabKeyWords, TabKeyWords.class, "热词");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_key_words:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabKeyWords.class);
    }

}
