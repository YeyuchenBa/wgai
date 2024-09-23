package org.jeecg.modules.quartz.job;

import org.jeecg.common.util.DateUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.tab.entity.TabAiSubscription;
import org.jeecg.modules.demo.tab.service.ITabAiHistoryService;
import org.jeecg.modules.demo.tab.service.ITabAiSubscriptionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.util.List;

/**
 * 示例不带参定时任务
 * 
 * @Author Scott
 */
@Slf4j
public class SampleJob implements Job {



	@Resource
	RedisUtil redisUtil;
	@Resource
	ITabAiSubscriptionService iTabAiSubscriptionService;
	@Resource
	ITabAiHistoryService iTabAiHistoryService;
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

		log.info("程序设备推送扫描任务："+jobExecutionContext.getJobDetail().getKey());
		log.info(String.format(" 程序设备推送扫描任务:" + DateUtils.getTimestamp()));
		List<TabAiSubscription> tabAiSubscription=iTabAiSubscriptionService.list();
		for (TabAiSubscription tab:tabAiSubscription) {
			if(tab.getRunState()!=null&&tab.getRunState()==1){ //为1说明在运行
				boolean flag=redisUtil.hasKey(tab.getId());
				if(flag){
					boolean runstate= (boolean) redisUtil.get(tab.getId());
					if(!runstate){ //未运行		//调用运行

						redisUtil.set(tab.getId(),true,3600); //设置执行3600内
						iTabAiHistoryService.startAiPush(tab);
					}
				}else{ //什么？ 我都没找到	//设置开启使用调用运行

						redisUtil.set(tab.getId(),true,3600); //设置执行3600内
						iTabAiHistoryService.startAiPush(tab);
				}
			}else{ //当前都为0了你执行个什么内容啊？

						redisUtil.set(tab.getId(),false,99999); //设置取消3600内
			}
		}
		log.info(" 运行设备程序内容："+tabAiSubscription.size());

	}
}
