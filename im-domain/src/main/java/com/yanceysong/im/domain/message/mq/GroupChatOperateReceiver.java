package com.yanceysong.im.domain.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import com.yanceysong.im.common.constant.RabbitmqConstants;
import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.model.content.GroupChatMessageContent;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.domain.message.service.GroupMessageService;
import com.yanceysong.im.domain.message.service.sync.MessageSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @ClassName GroupChatOperateReceiver
 * @Description 群聊消息接收器
 * @date 2023/5/16 10:39
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Component
public class GroupChatOperateReceiver extends AbstractChatOperateReceiver {

    @Resource
    private GroupMessageService groupMessageService;
    @Resource
    private MessageSyncService messageSyncServiceImpl;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitmqConstants.IM2_GROUP_SERVICE, durable = "true"),
                    exchange = @Exchange(value = RabbitmqConstants.IM2_GROUP_SERVICE, durable = "true")
            ),
            concurrency = "1"
    )
    public void onChatMessage(@Payload Message message,
                              @Headers Map<String, Object> headers,
                              Channel channel) throws Exception {
        process(message, headers, channel);
    }

    @Override
    protected void doStrategy(Integer command, JSONObject jsonObject, String message) {
        if (command.equals(GroupEventCommand.MSG_GROUP.getCommand())) {
            //处理消息
            GroupChatMessageContent messageContent
                    = jsonObject.toJavaObject(GroupChatMessageContent.class);
            groupMessageService.processor(messageContent);
        } else if (command.equals(GroupEventCommand.MSG_GROUP_READ.getCommand())) {
            // 消息已读接收确认
            MessageReadContent messageContent = JSON.parseObject(message, new TypeReference<MessageReadContent>() {
            }.getType());
            messageSyncServiceImpl.readMark(messageContent,
                    GroupEventCommand.MSG_GROUP_READ_NOTIFY,
                    GroupEventCommand.MSG_GROUP_READ_RECEIPT);
        }
    }


}
