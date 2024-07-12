package org.jeecg.modules.tab.AIModel.V5;

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
public class VideoSendReadCfgV5 {
    public static int QueueSize=10;
    public static LinkedBlockingQueue<MapTime> matlist=new LinkedBlockingQueue(QueueSize);
    public static  int StartTime=0;
    public static Map<String, TabAiBase> map=new HashMap<>();
    public VideoSendReadCfgV5(int QueueSize){
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

}

