package com.yanceysong.im.domain.message.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.AmqpHeaders;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @ClassName AbstractChatOperateReceiver
 * @Description 模板模式编排消息接收器定义解析流程
 * @date 2023/5/16 10:38
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractChatOperateReceiver {

    @Resource
    private P2PChatOperateReceiver p2PChatOperateReceiver;

    @Resource
    private GroupChatOperateReceiver groupChatOperateReceiver;

    /**
     * 消息接收器定义和解析流程
     *
     * @param message 消息
     * @param headers 头
     * @param channel mq的通道
     * @throws Exception 异常
     */
    public void process(Message message, Map<String, Object> headers, Channel channel) throws Exception {
        // 1. 接受 MQ 发布者传输的二进制 Msg 消息体, 并反序列化成 String
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        // 2. 打印日志记录
        log.info("MQ 队列 QUEUE 读取到消息 ::: [{}]", msg);
        // 3. 制作 ACK 头部帧
        // DELIVERY_TAG是消息投递序号
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        try {
            // 4. 解析 msg 为 JSON
            JSONObject jsonObject = JSON.parseObject(msg);
            // 5. 获取 msg 消息体里的 command 命令
            Integer command = jsonObject.getInteger("command");
            // 6. 根据 command 确定策略调用
            if (command.toString().startsWith("1")) {
                p2PChatOperateReceiver.doStrategy(command, jsonObject,msg);
            } else {
                groupChatOperateReceiver.doStrategy(command, jsonObject,msg);
            }
            // 7. 发送 ACK 成功应答报文
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理消息出现异常：{}", e.getMessage());
            log.error("RMQ_CHAT_TRAN_ERROR", e);
            log.error("NACK_MSG:{}", msg);
            //第一个false 表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        }
    }

    /**
     * 策略模式决定策略执行，具体实现交给子类实现
     *
     * @param command
     * @param jsonObject
     */
    protected abstract void doStrategy(Integer command, JSONObject jsonObject,String message);

}

