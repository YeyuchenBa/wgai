package org.jeecg.modules.tab.AIModel.V5Util;

import lombok.Data;
import org.jeecg.modules.demo.tab.entity.TabAiBase;
import org.jeecg.modules.tab.AIModel.V5.MapTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Administrator
 * @date 2024/3/22 20:35
 */
@Data
public class VideoSendReadCfgV5Util {
    public static int QueueSize=10;
    public static LinkedBlockingQueue<MapTimeUtil> matlist=new LinkedBlockingQueue(QueueSize);
    public static  int StartTime=0;
    public static Map<String, TabAiBase> map=new HashMap<>();
    public VideoSendReadCfgV5Util(int QueueSize){
        this.QueueSize=QueueSize;
        this.matlist=new LinkedBlockingQueue(QueueSize);;
    }

    /***
     * 转换为缓存处理
     * @param tabAiBaseList
     * @return
     */
    public static Map<String, TabAiBase> getMap(List<TabAiBase> tabAiBaseList){
        Map<String, TabAiBase> mapname=new HashMap<>();
        for (TabAiBase aibase:tabAiBaseList) {
            mapname.put(aibase.getEnglishName(),aibase);
        }
        return  mapname;
    }
    public static String millisecondsToHours(long milliseconds) {
        // 将毫秒转换为小时、分钟和秒
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = ((milliseconds % (1000 * 60 * 60)) % (1000 * 60)) / 1000;
        long millis = milliseconds % 1000;
        // 构造结果字符串
        String result = String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds,millis);
        return result;
    }
}

