package com.yanceysong.im.infrastructure.strategy.rabbitmq.listener;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.infrastructure.strategy.utils.MqFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @ClassName MqMessageListener
 * @Description
 * @date 2023/4/26 14:03
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class MqMessageListener {
    private static void startListenerMessage() {
        try {
            //mq的channel
            Channel channel = MqFactory.getChannel(Constants.RabbitmqConstants.MessageService2Im);
            channel.queueDeclare(Constants.RabbitmqConstants.MessageService2Im, true, false, false, null);
            channel.queueBind(Constants.RabbitmqConstants.MessageService2Im, Constants.RabbitmqConstants.MessageService2Im, "");
            channel.basicConsume(Constants.RabbitmqConstants.MessageService2Im
                    , false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            // TODO 处理消息服务发来的信息
                            String msgStr = new String(body);
                            log.info(msgStr);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始监听
     */
    public static void init() {
        startListenerMessage();
    }
}
