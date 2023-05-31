package com.yanceysong.im.infrastructure.rabbitmq.publish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.RabbitmqConstants;
import com.yanceysong.im.common.enums.command.CommandType;
import com.yanceysong.im.infrastructure.rabbitmq.MqFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName MqMessageProducer
 * @Description
 * @date 2023/4/26 14:04
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class MqMessageProducer {

    public static void sendMessage(Message message, Integer command) {
        String num = command.toString();
        String substring = num.substring(0, 1);
        CommandType commandType = CommandType.getCommandType(substring);
        String channelName = null;
        assert commandType != null;
        if (commandType.equals(CommandType.MESSAGE)) {
            channelName = RabbitmqConstants.IM2_MESSAGE_SERVICE;
        } else if (commandType.equals(CommandType.GROUP)) {
            channelName = RabbitmqConstants.IM2_GROUP_SERVICE;
        }
        Channel channel;
        try {
            channel = MqFactory.getChannel(channelName);
            // 解析私有协议的内容
            JSONObject o = (JSONObject) JSON.toJSON(message.getMessagePack());
            o.put("command", command);
            o.put("clientType", message.getMessageHeader().getClientType());
            o.put("imei", message.getMessageHeader().getImei());
            o.put("appId", message.getMessageHeader().getAppId());
            // TODO 开启镜像队列防止 MQ 丢失数据
            channel.basicPublish(channelName, "",
                    null, o.toJSONString().getBytes());
        } catch (Exception e) {
            log.error("发送消息出现异常：{}", e.getMessage());
        }
    }
}
