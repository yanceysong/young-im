package com.yanceysong.im.domain.message.strategy.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yanceysong.im.common.model.RecallMessageContent;
import com.yanceysong.im.domain.message.service.sync.MessageSyncService;
import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import com.yanceysong.im.domain.message.strategy.model.DomainCommandContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName MessageRecall
 * @Description
 * @date 2023/6/8 16:57
 * @Author yanceysong
 * @Version 1.0
 */
@Component
public class MessageRecall implements DomainCommandStrategy {
    @Resource
    private MessageSyncService messageSyncService;

    @Override
    public void doStrategy(DomainCommandContext domainCommandContext) {
        RecallMessageContent messageContent = JSON.parseObject(domainCommandContext.getMsg(), new TypeReference<RecallMessageContent>() {
        }.getType());
        messageSyncService.recallMessage(messageContent);
    }
}
