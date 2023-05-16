package com.yanceysong.im.infrastructure.rabbitmq.publish;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.infrastructure.utils.MqFactory;
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
        Channel channel = null;
        String channelName = Constants.RabbitmqConstants.IM2_MESSAGE_SERVICE;

        if (command.toString().startsWith("2")) {
            channelName = Constants.RabbitmqConstants.IM2_GROUP_SERVICE;
        }

        try {
            channel = MqFactory.getChannel(channelName);

            // 解析私有协议的内容
            JSONObject o = (JSONObject) JSON.toJSON(message.getMessagePack());
            o.put("command", command);
            o.put("clientType", message.getMessageHeader().getClientType());
            o.put("imei", message.getMessageHeader().getImei());
            o.put("appId", message.getMessageHeader().getAppId());

            channel.basicPublish(channelName, "",
                    null, o.toJSONString().getBytes());
        } catch (Exception e) {
            log.error("发送消息出现异常：{}", e.getMessage());
        }
    }

}
