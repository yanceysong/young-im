package com.yanceysong.im.domain.message.strategy.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.domain.message.service.sync.MessageSyncService;
import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import com.yanceysong.im.domain.message.strategy.model.DomainCommandContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName MessageGroupRead
 * @Description
 * @date 2023/6/8 17:19
 * @Author yanceysong
 * @Version 1.0
 */
@Component
public class MessageGroupRead implements DomainCommandStrategy {
    @Resource
    private MessageSyncService messageSyncService;

    @Override
    public void doStrategy(DomainCommandContext domainCommandContext) {
        // 消息已读接收确认
        MessageReadContent messageContent = JSON.parseObject(domainCommandContext.getMsg(), new TypeReference<MessageReadContent>() {
        }.getType());
        messageSyncService.readMark(messageContent,
                GroupEventCommand.MSG_GROUP_READ_NOTIFY,
                GroupEventCommand.MSG_GROUP_READ_RECEIPT);
    }
}
