package org.jeecg.modules.demo.chat.config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysConfig {
    private int delSentenceNubTh = 20;//删除条目数阈值
    private int delKeywordNubTh = 30;
}
