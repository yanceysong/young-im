package com.yanceysong.im.domain.message.service.sync;

import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.content.MessageReceiveAckContent;
import com.yanceysong.im.common.model.read.MessageReadContent;

/**
 * @ClassName MessageSyncService
 * @Description
 * @date 2023/5/17 13:50
 * @Author yanceysong
 * @Version 1.0
 */
public interface MessageSyncService {

    /**
     * 在线目标用户同步接收消息确认
     * 在 {@link com.yanceysong.im.domain.message.mq.P2PChatOperateReceiver}
     * 和 {@link com.yanceysong.im.domain.message.mq.GroupChatOperateReceiver} 里被调度
     * @param pack
     */
    void receiveMark(MessageReceiveAckContent pack);

    /**
     * 消息已读功能
     * 在 {@link com.yanceysong.im.domain.message.mq.P2PChatOperateReceiver}
     * 和 {@link com.yanceysong.im.domain.message.mq.GroupChatOperateReceiver} 里被调度
     * 1. 更新会话 Seq
     * 2. 通知在线同步端发送指定 command
     * 3. 发送已读回执通知原消息发送方
     * @param messageContent
     * @param notify 消息已读 TCP 通知【同步接收所有端】
     * @param receipt 消息已读回执 TCP 通知 【发送给原消息发送方】
     */
    void readMark(MessageReadContent messageContent, Command notify, Command receipt);

}