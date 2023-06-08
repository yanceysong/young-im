package com.yanceysong.im.domain.message.strategy.command;

import com.yanceysong.im.common.model.content.GroupChatMessageContent;
import com.yanceysong.im.domain.message.service.GroupMessageService;
import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import com.yanceysong.im.domain.message.strategy.model.DomainCommandContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName MessageGroup
 * @Description
 * @date 2023/6/8 17:17
 * @Author yanceysong
 * @Version 1.0
 */
@Component
public class MessageGroup implements DomainCommandStrategy {
    @Resource
    private GroupMessageService groupMessageService;

    @Override
    public void doStrategy(DomainCommandContext domainCommandContext) {
        //处理消息
        GroupChatMessageContent messageContent
                = domainCommandContext.getJson().toJavaObject(GroupChatMessageContent.class);
        groupMessageService.processor(messageContent);
    }
}
