package com.yanceysong.im.domain.message.strategy.command;

import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.domain.message.service.sync.MessageSyncService;
import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import com.yanceysong.im.domain.message.strategy.model.DomainCommandContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName MessageRead
 * @Description
 * @date 2023/6/8 16:57
 * @Author yanceysong
 * @Version 1.0
 */
@Component
public class MessageRead implements DomainCommandStrategy {
    @Resource
    private MessageSyncService messageSyncServiceImpl;
    @Override
    public void doStrategy(DomainCommandContext domainCommandContext) {
        // 消息已读确认
        MessageReadContent messageContent
                = domainCommandContext.getJson().toJavaObject(MessageReadContent.class);
        messageSyncServiceImpl.readMark(messageContent,
                MessageCommand.MSG_READ_NOTIFY, MessageCommand.MSG_READ_RECEIPT);
    }
}
