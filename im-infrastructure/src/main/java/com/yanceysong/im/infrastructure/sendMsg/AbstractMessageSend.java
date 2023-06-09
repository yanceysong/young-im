package com.yanceysong.im.infrastructure.sendMsg;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.proto.MessagePack;
import com.yanceysong.im.common.constant.RabbitmqConstants;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.common.ClientInfo;
import com.yanceysong.im.common.model.user.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName AbstractMessageSend
 * @Description 抽象类完成基础功能的实现，以及定制化方法的抽象定义
 * @date 2023/5/12 10:55
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public abstract class AbstractMessageSend implements MessageSend {
    /**
     * 队列：服务端与客户端之间的消息投递
     */
    private final String queueName = RabbitmqConstants.MESSAGE_SERVICE2_IM;
    @Resource
    RabbitTemplate rabbitTemplate;

    @Override
    public boolean sendMessage(String receiverId, Command command, Object msg, UserSession session) {
        // 将具体消息以及其他关键信息头封装成数据包，指定该消息应发送的 channel 消息通道
        MessagePack<Object> messagePack = new MessagePack<>();
        messagePack.setCommand(command.getCommand());
        messagePack.setReceiverId(receiverId);
        messagePack.setClientType(session.getClientType());
        messagePack.setAppId(session.getAppId());
        messagePack.setImei(session.getImei());
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(msg));
        messagePack.setData(jsonObject);
        // 将数据包转换成 JSON 对象发送出去
        String body = JSONObject.toJSONString(messagePack);
        return sendMessage(session, body);
    }

    /**
     * 具体发送逻辑
     *
     * @param session 用户的信息
     * @param msg     具体的消息
     * @return 发送结果
     */
    private boolean sendMessage(UserSession session, Object msg) {
        // 行为埋点
        try {
            log.info("send message {} ", msg);
            // MQ 发送消息
            rabbitTemplate.convertAndSend(queueName, session.getBrokerId() + "", msg);
            return true;
        } catch (AmqpException e) {
            log.error("send error {} ", e.getMessage());
            return false;
        }
    }

    /**
     * 将消息发送给所有端，用于消息同步[对自己，对他人]
     *
     * @param receiverId    要发送的消息目标人的id
     * @param command 指令
     * @param data    数据
     * @param appId   APP的id
     * @return 成功发送的客户端
     */
    public abstract List<ClientInfo> sendToUserAllClient(String receiverId, Command command, Object data, Integer appId);

    /**
     * 将消息发送给指定端
     *
     * @param receiverId       要发送的消息目标人的id
     * @param command    指令
     * @param data       要发的数据
     * @param clientInfo 指定的客户端
     */
    public abstract void sendToUserOneClient(String receiverId, Command command, Object data, ClientInfo clientInfo);

    /**
     * 将消息发送给除了指定端的其他端
     *
     * @param receiverId       要发送的消息目标人的id
     * @param command    指令
     * @param data       要发的数据
     * @param clientInfo 除了的客户端
     */
    public abstract void sendToUserExceptClient(String receiverId, Command command, Object data, ClientInfo clientInfo);
}

