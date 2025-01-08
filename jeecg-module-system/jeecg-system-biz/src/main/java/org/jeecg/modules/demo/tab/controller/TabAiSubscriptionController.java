package org.jeecg.modules.demo.tab.controller;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.demo.easy.entity.TabEasyConfig;
import org.jeecg.modules.demo.tab.entity.PushInfo;
import org.jeecg.modules.demo.tab.entity.TabAiModelBund;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.jeecg.modules.demo.tab.service.ITabAiBaseService;
import org.jeecg.modules.demo.tab.service.ITabAiSubscriptionService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.service.impl.TabAiModelServiceImpl;
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
 * @Description: Ai事件订阅
 * @Author: WGAI
 * @Date:   2024-04-08
 * @Version: V1.0
 */
@Api(tags="Ai事件订阅")
@RestController
@RequestMapping("/tab/tabAiSubscription")
@Slf4j
public class TabAiSubscriptionController extends JeecgController<TabAiSubscription, ITabAiSubscriptionService> {
	@Autowired
	private ITabAiSubscriptionService tabAiSubscriptionService;
	@Autowired
	private TabAiModelServiceImpl tabAiModelServiceImpl;
	@Autowired
	RedisTemplate  redisTemplate;
	 @Autowired
	 private ITabAiBaseService tabAiBaseService;

	@ApiOperation(value="Ai事件订阅-test", notes="Ai事件订阅-test")
	@RequestMapping(value = "/subInfo")
	public List<PushInfo>  getSub(){
		log.info("输出结果");
		 List<PushInfo> object1= (List<PushInfo> ) redisTemplate.opsForValue().get("sendPush");
		 //中文写入缓存内容
		 tabAiBaseService.SendRedisBase();

		 AIModelYolo3  modelYolo3=new AIModelYolo3();
		 modelYolo3.SendPicThread(redisTemplate);
		return object1;
	}
	 @ApiOperation(value="Ai事件订阅-test", notes="Ai事件订阅-test")
	 @PostMapping(value = "/subInfo2")
	 public JSONObject getSub2(@RequestBody JSONObject jsonObject){
		 log.info("输出结果"+jsonObject.getString("type"));
	

		 return jsonObject;
	 }

	/**
	 * 分页列表查询
	 *
	 * @param tabAiSubscription
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "Ai事件订阅-分页列表查询")
	@ApiOperation(value="Ai事件订阅-分页列表查询", notes="Ai事件订阅-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<TabAiSubscription>> queryPageList(TabAiSubscription tabAiSubscription,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<TabAiSubscription> queryWrapper = QueryGenerator.initQueryWrapper(tabAiSubscription, req.getParameterMap());
		Page<TabAiSubscription> page = new Page<TabAiSubscription>(pageNo, pageSize);
		IPage<TabAiSubscription> pageList = tabAiSubscriptionService.page(page, queryWrapper);
		List<String> typeids = pageList.getRecords().stream().map(TabAiSubscription::getEventTypes).collect(Collectors.toList());
		if(typeids!=null && typeids.size()>0){
			Map<String, String> res = new HashMap(typeids.size());
			for (String a:typeids) {
				if(a!=null){
					List<TabAiModel> list= tabAiModelServiceImpl.listByIds(Arrays.asList(a.split(",")));
					res.put(a,list.stream().map(TabAiModel::getAiName).collect(Collectors.joining(",")));
				}
			}
			pageList.getRecords().forEach(item->{
				item.setEventTypesName(res.get(item.getEventTypes()));
			});
		}

		return Result.OK(pageList);
	}



	 /**
	  *   查询
	  *
	  * @param tabAiSubscription
	  * @return
	  */
	 @AutoLog(value = "Ai事件订阅-查询")
	 @ApiOperation(value="Ai事件订阅-查询", notes="Ai事件订阅-查询")
	 //@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:add")
	 @PostMapping(value = "/getPushUrl")
	 public Result<List<TabAiSubscription>> getPushUrl(@RequestBody List<TabAiSubscription> tabAiSubscription) {
		 List<TabAiSubscription> list=new ArrayList<>();
		 for (TabAiSubscription tabAI:tabAiSubscription) {
			 LambdaQueryWrapper<TabAiSubscription> queryWrapper=new LambdaQueryWrapper<>();
			 queryWrapper.eq(TabAiSubscription::getEventUrl,tabAI.getEventUrl());
			 queryWrapper.eq(TabAiSubscription::getRemake,tabAI.getRemake());
			 TabAiSubscription tabAiSubscription1=tabAiSubscriptionService.getOne(queryWrapper);
			 list.add(tabAiSubscription1);
		 }


		 return Result.OK("成功",list);
	 }

	/**
	 *   添加
	 *
	 * @param tabAiSubscription
	 * @return
	 */
	@AutoLog(value = "Ai事件订阅-添加")
	@ApiOperation(value="Ai事件订阅-添加", notes="Ai事件订阅-添加")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody TabAiSubscription tabAiSubscription) {
		LambdaQueryWrapper<TabAiSubscription> queryWrapper=new LambdaQueryWrapper<>();
		queryWrapper.eq(TabAiSubscription::getEventUrl,tabAiSubscription.getEventUrl());
		queryWrapper.eq(TabAiSubscription::getRemake,tabAiSubscription.getRemake());
		TabAiSubscription tabAiSubscription1=tabAiSubscriptionService.getOne(queryWrapper);
		if(tabAiSubscription1!=null){
			return Result.error("已经存在请调用修改！");
		}
		tabAiSubscription.setRunState(0);
		boolean flag=tabAiSubscriptionService.save(tabAiSubscription);
		if(flag) {
			tabAiSubscriptionService.insertRedisSubscription();
		}
		return Result.OK("添加成功！");
	}

	/**
	 *  编辑
	 *
	 * @param tabAiSubscription
	 * @return
	 */
	@AutoLog(value = "Ai事件订阅-编辑")
	@ApiOperation(value="Ai事件订阅-编辑", notes="Ai事件订阅-编辑")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody TabAiSubscription tabAiSubscription) {

		tabAiSubscriptionService.updateById(tabAiSubscription);
		tabAiSubscriptionService.insertRedisSubscription();
		return Result.OK("执行成功!");
	}

	 /**
	  *  编辑
	  *
	  * @param tabAiSubscription
	  * @return
	  */
	 @AutoLog(value = "Ai事件订阅-编辑WithPushUrl")
	 @ApiOperation(value="Ai事件订阅-编辑WithPushUrl", notes="Ai事件订阅-编辑WithPushUrl")
	 //@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:edit")
	 @RequestMapping(value = "/editWithPushUrl", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> editWithPushUrl(@RequestBody TabAiSubscription tabAiSubscription) {

		 LambdaQueryWrapper<TabAiSubscription> queryWrapper=new LambdaQueryWrapper<>();
		 queryWrapper.eq(TabAiSubscription::getEventUrl,tabAiSubscription.getEventUrl());
		 queryWrapper.eq(TabAiSubscription::getRemake,tabAiSubscription.getRemake());
		 TabAiSubscription tabAiSubscription1=tabAiSubscriptionService.getOne(queryWrapper);
		 boolean flag=tabAiSubscriptionService.updateById(tabAiSubscription1);
		 if(flag) {
			 tabAiSubscriptionService.insertRedisSubscription();
		 }
		 return Result.OK("编辑成功!");
	 }


	 /**
	  *  删除
	  *
	  * @param tabAiSubscription
	  * @return
	  */
	 @AutoLog(value = "Ai事件订阅-删除WithPushUrl")
	 @ApiOperation(value="Ai事件订阅-删除WithPushUrl", notes="Ai事件订阅-删除WithPushUrl")
	 //@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:edit")
	 @RequestMapping(value = "/delWithPushUrl", method = {RequestMethod.PUT,RequestMethod.POST})
	 public Result<String> delWithPushUrl(@RequestBody TabAiSubscription tabAiSubscription) {

		 LambdaQueryWrapper<TabAiSubscription> queryWrapper=new LambdaQueryWrapper<>();
		 queryWrapper.eq(TabAiSubscription::getEventUrl,tabAiSubscription.getEventUrl());
		 queryWrapper.eq(TabAiSubscription::getRemake,tabAiSubscription.getRemake());
		 TabAiSubscription tabAiSubscription1=tabAiSubscriptionService.getOne(queryWrapper);
		 boolean flag=tabAiSubscriptionService.removeById(tabAiSubscription1);
		 if(flag) {
			 tabAiSubscriptionService.insertRedisSubscription();
		 }
		 return Result.OK("删除成功!");
	 }

	 /**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "Ai事件订阅-通过id删除")
	@ApiOperation(value="Ai事件订阅-通过id删除", notes="Ai事件订阅-通过id删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		tabAiSubscriptionService.removeById(id);
		tabAiSubscriptionService.insertRedisSubscription();
		return Result.OK("删除成功!");
	}



	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "Ai事件订阅-批量删除")
	@ApiOperation(value="Ai事件订阅-批量删除", notes="Ai事件订阅-批量删除")
	//@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.tabAiSubscriptionService.removeByIds(Arrays.asList(ids.split(",")));
		tabAiSubscriptionService.insertRedisSubscription();
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "Ai事件订阅-通过id查询")
	@ApiOperation(value="Ai事件订阅-通过id查询", notes="Ai事件订阅-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<TabAiSubscription> queryById(@RequestParam(name="id",required=true) String id) {
		TabAiSubscription tabAiSubscription = tabAiSubscriptionService.getById(id);
		if(tabAiSubscription==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(tabAiSubscription);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param tabAiSubscription
    */
    //@RequiresPermissions("org.jeecg.modules.demo:tab_ai_subscription:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, TabAiSubscription tabAiSubscription) {
        return super.exportXls(request, tabAiSubscription, TabAiSubscription.class, "Ai事件订阅");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    //@RequiresPermissions("tab_ai_subscription:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, TabAiSubscription.class);
    }

}
