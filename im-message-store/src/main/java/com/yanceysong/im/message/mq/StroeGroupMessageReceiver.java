package com.yanceysong.im.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.yanceysong.im.common.constant.RabbitmqConstants;
import com.yanceysong.im.common.constant.ThreadPoolConstants;
import com.yanceysong.im.common.thradPool.ThreadPoolFactory;
import com.yanceysong.im.message.dao.ImMessageBodyEntity;
import com.yanceysong.im.message.model.DoStoreGroupMessageDto;
import com.yanceysong.im.message.service.StoreMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName StroeGroupMessageReceiver
 * @Description
 * @date 2023/5/16 11:07
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
@Service
public class StroeGroupMessageReceiver {


    @Qualifier("mysql")
    private StoreMessageService storeMessageService;
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = RabbitmqConstants.STORE_GROUP_MESSAGE, durable = "true"),
                    exchange = @Exchange(value = RabbitmqConstants.STORE_GROUP_MESSAGE, durable = "true")
            ), concurrency = "1"
    )
    public void onChatMessage(@Payload Message message,
                              @Headers Map<String, Object> headers,
                              Channel channel) {
        ThreadPoolFactory.getThreadPool(ThreadPoolConstants.RABBITMQ_LISTENER_GROUP, false).submit(() -> {
            String msg = new String(message.getBody(), StandardCharsets.UTF_8);
            log.info("[GROUP 消息存储] MQ 队列 QUEUE 读取到消息 ::: [{}]", msg);
            Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
            try {
                JSONObject jsonObject = JSON.parseObject(msg);
                DoStoreGroupMessageDto doStoreGroupMessageDto = jsonObject.toJavaObject(DoStoreGroupMessageDto.class);
                ImMessageBodyEntity messageBody = jsonObject.getObject("messageBody", ImMessageBodyEntity.class);
                doStoreGroupMessageDto.setImMessageBodyEntity(messageBody);
                storeMessageService.doStoreGroupMessage(doStoreGroupMessageDto);
                channel.basicAck(deliveryTag, false);
            } catch (Exception e) {
                log.error("处理消息出现异常：{}", e.getMessage());
                log.error("RMQ_CHAT_TRAN_ERROR", e);
                log.error("NACK_MSG:{}", msg);
                //第一个false 表示不批量拒绝，第二个false表示不重回队列
                try {
                    channel.basicNack(deliveryTag, false, false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


    }
}

