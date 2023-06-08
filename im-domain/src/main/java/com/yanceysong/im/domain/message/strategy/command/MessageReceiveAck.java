package com.yanceysong.im.domain.message.strategy.command;

import com.yanceysong.im.common.model.content.MessageReceiveAckContent;
import com.yanceysong.im.domain.message.service.sync.MessageSyncService;
import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import com.yanceysong.im.domain.message.strategy.model.DomainCommandContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName MessageReceiveAck
 * @Description
 * @date 2023/6/8 16:54
 * @Author yanceysong
 * @Version 1.0
 */
@Component
public class MessageReceiveAck implements DomainCommandStrategy {

    @Resource
    private MessageSyncService messageSyncServiceImpl;

    @Override
    public void doStrategy(DomainCommandContext domainCommandContext) {
        // 消息接收确认
        MessageReceiveAckContent messageReceiveAckContent
                = domainCommandContext.getJson().toJavaObject(MessageReceiveAckContent.class);
        messageSyncServiceImpl.receiveMark(messageReceiveAckContent);
    }
}
