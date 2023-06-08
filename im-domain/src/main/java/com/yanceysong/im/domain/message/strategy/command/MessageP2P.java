package com.yanceysong.im.domain.message.strategy.command;

import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.domain.message.service.P2PMessageService;
import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import com.yanceysong.im.domain.message.strategy.model.DomainCommandContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName MessageP2P
 * @Description
 * @date 2023/6/8 16:58
 * @Author yanceysong
 * @Version 1.0
 */
@Component
public class MessageP2P implements DomainCommandStrategy {
    @Resource
    private P2PMessageService p2pMessageService;
    @Override
    public void doStrategy(DomainCommandContext domainCommandContext) {
        // 处理消息
        MessageContent messageContent
                = domainCommandContext.getJson().toJavaObject(MessageContent.class);
        p2pMessageService.processor(messageContent);
    }
}
