package com.yanceysong.im.domain.message.service;

import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.enums.message.MessageReceiveAckPack;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName MessageSyncService
 * @Description 消息同步服务类
 * 用于处理消息接收确认，同步等操作
 * @date 2023/5/17 11:27
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class MessageSyncService {

    @Resource
    private MessageProducer messageProducer;

    /**
     * 在线目标用户同步接收消息确认
     *
     * @param pack
     */
    public void receiveMark(MessageReceiveAckPack pack) {
        // 确认接收 ACK 发送给在线目标用户全端
        messageProducer.sendToUserAllClient(pack.getToId(),
                MessageCommand.MSG_RECEIVE_ACK, pack, pack.getAppId());
    }

}