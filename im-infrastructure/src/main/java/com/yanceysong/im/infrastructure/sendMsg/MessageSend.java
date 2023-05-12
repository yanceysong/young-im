package com.yanceysong.im.infrastructure.sendMsg;

import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.UserSession;

/**
 * @ClassName MessageSend
 * @Description
 * @date 2023/5/12 10:56
 * @Author yanceysong
 * @Version 1.0
 */
public interface MessageSend {
    /**
     * 发送消息数据包给 TCP 网关
     *
     * @param toId    要发送的人的id
     * @param command 指令
     * @param msg     消息
     * @param session 用户信息
     * @return 是否发送成功
     */
    boolean sendMessage(String toId, Command command, Object msg, UserSession session);
}
