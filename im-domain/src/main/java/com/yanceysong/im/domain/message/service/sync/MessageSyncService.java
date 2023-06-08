package com.yanceysong.im.domain.message.service.sync;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.RecallMessageContent;
import com.yanceysong.im.common.model.content.MessageReceiveAckContent;
import com.yanceysong.im.common.model.content.OfflineMessageContent;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.common.model.sync.SyncReq;
import com.yanceysong.im.common.model.sync.SyncResp;
import com.yanceysong.im.domain.message.mq.P2PChatOperateReceiver;

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
     * 在 {@link P2PChatOperateReceiver}
     * 和 {@link com.yanceysong.im.domain.message.mq.GroupChatOperateReceiver} 里被调度
     *
     * @param pack 包
     */
    void receiveMark(MessageReceiveAckContent pack);

    /**
     * 消息已读功能
     * 在 {@link P2PChatOperateReceiver}
     * 和 {@link com.yanceysong.im.domain.message.mq.GroupChatOperateReceiver} 里被调度
     * 1. 更新会话 Seq
     * 2. 通知在线同步端发送指定 command
     * 3. 发送已读回执通知原消息发送方
     *
     * @param messageContent 消息上下文
     * @param notify         消息已读 TCP 通知【同步接收所有端】
     * @param receipt        消息已读回执 TCP 通知 【发送给原消息发送方】
     */
    void readMark(MessageReadContent messageContent, Command notify, Command receipt);

    /**
     * 增量拉取离线消息功能
     *
     * @param req 请求
     * @return 增量消息
     */
    ResponseVO<SyncResp<OfflineMessageContent>> syncOfflineMessage(SyncReq req);

    /**
     * 撤回消息
     * 修改历史消息的状态
     * 修改离线消息的状态
     * ack给发送方
     * 发送给同步端
     * 分发给消息的接收方
     *
     * @param content 请求上下文
     */
    void recallMessage(RecallMessageContent content);
}