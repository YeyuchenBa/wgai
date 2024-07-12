package org.jeecg.modules.demo.tab.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.demo.tab.entity.TabAiHistory;
import org.jeecg.modules.demo.tab.entity.TabAiModelBund;
import org.jeecg.modules.demo.tab.mapper.TabAiBaseMapper;
import org.jeecg.modules.demo.tab.mapper.TabAiHistoryMapper;
import org.jeecg.modules.demo.tab.service.ITabAiHistoryService;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.monitor.service.RedisService;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.tab.AIModel.AIModelYolo3;
import org.jeecg.modules.tab.AIModel.VideoSendReadCfg;
import org.jeecg.modules.tab.entity.TabAiModel;
import org.jeecg.modules.tab.mapper.TabAiModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: AI识别结果历史
 * @Author: jeecg-boot
 * @Date:   2024-03-13
 * @Version: V1.0
 */
@Service
@Slf4j
public class TabAiHistoryServiceImpl extends ServiceImpl<TabAiHistoryMapper, TabAiHistory> implements ITabAiHistoryService {

    @Autowired
    TabAiBaseMapper tabAiBaseMapper;
    @Autowired
    TabAiModelMapper modelMapper;
    @Autowired
    TabAiHistoryMapper tabAiHistoryMapper;

    @Resource
    WebSocket webSocket;
    @Resource
    RedisUtil redisUtil;

    @Resource
    RedisTemplate redisTemplate;

    @Override
    public int saveCarIdentify(TabAiModelBund tabAiModelBund, String path) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendPicOpencvCar(tabAiModel1.getAiWeights(),tabAiModelBund.getSaveUrl(),null,path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }

    }

    @Override
    public int saveIdentify(TabAiModelBund tabAiModelBund,String path) {

        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
           String savePath=modelYolo3.SendPicYoloV3(tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSaveUrl(),null,path);
           if(savePath.equals("error")){
               return 1;
           }
           log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }

    }

    @Override
    public int saveIdentifyYolov5(TabAiModelBund tabAiModelBund, String path) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendPicYoloV5(tabAiModel1.getAiWeights(),tabAiModel1.getAiNameName(),tabAiModelBund.getSaveUrl(),null,path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
    }

    @Override
    public int saveIdentifyYolov8(TabAiModelBund tabAiModelBund, String path) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendPicYoloV5(tabAiModel1.getAiWeights(),tabAiModel1.getAiNameName(),tabAiModelBund.getSaveUrl(),null,path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
    }

    @Override
    public int closedentify(TabAiModelBund tabAiModelBund, LoginUser sysUser) {
        //使用redis 设置关闭当前流 使用的用户关闭识别 超时时间设置3s失效
        redisUtil.set(tabAiModelBund.getSendUrl()+""+sysUser.getId(),false);
        redisUtil.expire(tabAiModelBund.getSendUrl()+""+sysUser.getId(),3000);
        //
        redisUtil.set(tabAiModelBund.getSendUrl()+"time"+sysUser.getId(),0);
        redisUtil.expire(tabAiModelBund.getSendUrl()+"time"+sysUser.getId(),3000);
        return 0;
    }

    @Override
    public int saveIdentifyVideo(TabAiModelBund tabAiModelBund, String path) {

        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        try {
            String savePath=modelYolo3.SendVideoYoloV3(tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path);
            if(savePath.equals("error")){
                return 1;
            }
            log.info("识别存储文件地址{}",savePath);
            Long b=System.currentTimeMillis();
            TabAiHistory tabAiHistory=new TabAiHistory();
            tabAiHistory.setBundName(tabAiModel1.getAiName());
            tabAiHistory.setModelName(tabAiModel1.getAiName());
            tabAiHistory.setModelId(tabAiModelBund.getModelName());
            tabAiHistory.setSendUrl("temp/"+savePath);
            tabAiHistory.setSendTime(b-a+"");
            tabAiHistoryMapper.insert(tabAiHistory);
            return 0;
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
    }

    @Override
    public int saveIdentifyLocalVideo(TabAiModelBund tabAiModelBund, String path,String userId) {
        Long a=System.currentTimeMillis();
        LambdaQueryWrapper< TabAiModel> query = new LambdaQueryWrapper<>();
        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        redisUtil.set(tabAiModelBund.getSendUrl()+""+userId,true);
        redisUtil.expire(tabAiModelBund.getSendUrl()+""+userId,( 24*60*60*365*1000));
        try {
            String savePath=modelYolo3.SendVideoLocalhostYoloV3(userId,tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path,webSocket,redisUtil);
            if(savePath.equals("error")){
                return 1;
            }
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public int saveIdentifyLocalVideoThread(TabAiModelBund tabAiModelBund, String path, String userId) {

        TabAiModel tabAiModel1=modelMapper.selectById(tabAiModelBund.getModelName());
        AIModelYolo3  modelYolo3=new AIModelYolo3();
        //处置结束符
        redisUtil.set(tabAiModelBund.getSendUrl()+""+userId,true);
        redisUtil.expire(tabAiModelBund.getSendUrl()+""+userId,( 24*60*60*365*1000));
        //处置时间戳
        redisUtil.set(tabAiModelBund.getSendUrl()+"time"+userId,0);
        redisUtil.expire(tabAiModelBund.getSendUrl()+"time"+userId,( 24*60*60*365*1000));
        //处置基础库
        Object object = redisUtil.get("AIModelBase");
        if(object==null){
            LambdaQueryWrapper< TabAiBase> querybase = new LambdaQueryWrapper<>();
            List<TabAiBase> base=tabAiBaseMapper.selectList(querybase);
            redisUtil.set("AIModelBase", JSONObject.toJSONString(base));
            redisUtil.expire("AIModelBase",(24*60*60*365*1000));
            VideoSendReadCfg.map=VideoSendReadCfg.getMap(base);
        }else{
            String jsonObject= object.toString();
            JSONArray array=JSONArray.parseArray(jsonObject);
            List<TabAiBase> base= array.toJavaList(TabAiBase.class);
            VideoSendReadCfg.map=VideoSendReadCfg.getMap(base);
        }
        try {

            String savePath=modelYolo3.SendVideoLocalhostYoloV3Thread(userId,tabAiModel1.getAiWeights(),tabAiModel1.getAiConfig(),tabAiModel1.getAiNameName(),tabAiModelBund.getSendUrl(),path,webSocket,redisUtil,redisTemplate);
            if(savePath.equals("error")){
                return 1;
            }
        }catch (Exception ex){
            log.warn("出错{}",ex);
            ex.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public Result<String>  startAi(TabAiModelBund tabAiModelBund, String path, String userId) {


        TabAiModel aiModel=modelMapper.selectById(tabAiModelBund.getModelName());
        if(aiModel!=null){


            switch (aiModel.getSpareOne()){
                case "1": //v3
                {
                    log.info("【进入V3开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceOne().equals("0")){ //当前为图片
                        int a=this.saveIdentify(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }else{
                        // 输出视频
                        // tabAiHistoryService.saveIdentifyVideo(tabAiModelBund,uploadpath);
                        // 输出坐标 延迟3-5s
                        //tabAiHistoryService.saveIdentifyLocalVideo(tabAiModelBund,uploadpath,sysUser.getId());
                        //多线程输出坐标
                        this.saveIdentifyLocalVideoThread(tabAiModelBund,path,userId);
                        return Result.OK("视频识别开始");
                    }
                }
                case "2":
                {
                    log.info("【进入V5开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceOne().equals("0")){ //当前为图片
                        int a=this.saveIdentifyYolov5(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }else{
                        // 输出视频
                        // tabAiHistoryService.saveIdentifyVideo(tabAiModelBund,uploadpath);
                        // 输出坐标 延迟3-5s
                        //tabAiHistoryService.saveIdentifyLocalVideo(tabAiModelBund,uploadpath,sysUser.getId());
                        //多线程输出坐标
                        this.saveIdentifyLocalVideoThread(tabAiModelBund,path,userId);
                        return Result.OK("视频识别开始");
                    }

                }//v5
                case "3":
                {  log.info("【进入V8开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    log.info("【进入V5开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceOne().equals("0")){ //当前为图片
                        int a=this.saveIdentifyYolov8(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }else{
                        // 输出视频
                        // tabAiHistoryService.saveIdentifyVideo(tabAiModelBund,uploadpath);
                        // 输出坐标 延迟3-5s
                        //tabAiHistoryService.saveIdentifyLocalVideo(tabAiModelBund,uploadpath,sysUser.getId());
                        //多线程输出坐标
                        this.saveIdentifyLocalVideoThread(tabAiModelBund,path,userId);
                        return Result.OK("视频识别开始");
                    }
                }//v8
                case "4": {}//json
                case "5": {}//other
                case "6": { //cv
                    log.info("【进入cv开始识别内容】{}",tabAiModelBund.getSpaceTwo());
                    if(tabAiModelBund.getSpaceTwo().equals("车牌识别")){
                        int a=this.saveCarIdentify(tabAiModelBund,path);
                        if(a==0){
                            return Result.OK("识别图片成功！");
                        }
                    }
                }//json
            }



        }
        return Result.error("识别失败未发现识别内容");
    }
}
