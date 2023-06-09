package com.yanceysong.im.infrastructure.rabbitmq.listener;

import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.yanceysong.im.codec.proto.MessagePack;
import com.yanceysong.im.common.constant.RabbitmqConstants;
import com.yanceysong.im.infrastructure.rabbitmq.MqFactory;
import com.yanceysong.im.infrastructure.rabbitmq.process.BaseProcess;
import com.yanceysong.im.infrastructure.rabbitmq.process.ProcessFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName MqMessageListener
 * @Description
 * @date 2023/4/26 14:03
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class MqMessageListener {
    public static String brokerId;

    /**
     * 每一个服务器节点，都绑定一个对应的queue，格式为 MessageService2Im + brokerId（每一个服务端的唯一编号）
     */
    private static void startListenerMessage() {
        try {
            //mq的channel broker Id 不同会有不同的队列
            Channel channel = MqFactory.getChannel(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId);
            channel.queueDeclare(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId, true, false, false, null);
            channel.queueBind(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId, RabbitmqConstants.MESSAGE_SERVICE2_IM, "");
            channel.basicConsume(RabbitmqConstants.MESSAGE_SERVICE2_IM + brokerId
                    , false,
                    new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String msgStr = new String(body, StandardCharsets.UTF_8);
                            try {
                                log.info("服务端监听消息信息为 {} ", msgStr);
                                // 消息写入数据通道
                                MessagePack messagePack = JSONObject.parseObject(msgStr, MessagePack.class);
                                BaseProcess messageProcess = ProcessFactory.getMessageProcess(messagePack.getCommand());
                                messageProcess.process(messagePack);
                                // 消息成功写入通道后发送应答 Ack
                                channel.basicAck(envelope.getDeliveryTag(), false);

                            } catch (Exception e) {
                                e.printStackTrace();
                                // 消息不能正常写入通道，发送失败应答 NAck
                                channel.basicNack(envelope.getDeliveryTag(), false, false);
                            }
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
    public static void init(String brokerId) {
        if (StringUtils.isBlank(MqMessageListener.brokerId)) {
            MqMessageListener.brokerId = brokerId;
        }
        startListenerMessage();
    }
}
