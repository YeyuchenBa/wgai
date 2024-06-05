package org.jeecg.modules.demo.tab.entity;

import lombok.Data;
import org.jeecg.modules.tab.entity.TabAiModel;

import java.util.List;

/**
 * @author Administrator
 * @date 2024/4/9 9:24
 */
@Data
public class PushInfo {

        public String pushId;
        public String videoURL;
        public String pushUrl;
        public List<TabAiModel> tabAiModelList;

        public int time;

        public String indexCode;
}
