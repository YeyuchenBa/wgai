package org.jeecg.modules.message.handle;

import org.jeecg.common.api.dto.message.MessageDTO;

/**
 * @Description: 发送信息接口
 * @Author: WGAI
 */
public interface ISendMsgHandle {

    /**
     * 发送信息
     * @param esReceiver 接受人
     * @param esTitle 标题
     * @param esContent 内容
     */
	void sendMsg(String esReceiver, String esTitle, String esContent);

    /**
     * 发送信息
     * @param messageDTO
     */
	default void sendMessage(MessageDTO messageDTO){

    }
}
