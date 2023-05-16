package com.yanceysong.im.infrastructure.strategy.command.message;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.pack.ChatMessageAck;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.codec.proto.MessagePack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.model.CheckSendMessageReq;
import com.yanceysong.im.infrastructure.feign.FeignMessageService;
import com.yanceysong.im.infrastructure.rabbitmq.publish.MqMessageProducer;
import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecutionRequest;
import io.netty.channel.ChannelHandlerContext;

/**
 * @ClassName GroupMsgCommand
 * @Description TCP 层校验消息发送方合法性
 * @date 2023/5/16 11:24
 * @Author yanceysong
 * @Version 1.0
 */
public class GroupMsgCommand extends BaseCommandStrategy {

    @Override
    public void systemStrategy(CommandExecutionRequest commandExecutionRequest) {
        ChannelHandlerContext ctx = commandExecutionRequest.getCtx();
        Message msg = commandExecutionRequest.getMsg();
        FeignMessageService feignMessageService = commandExecutionRequest.getFeignMessageService();

        CheckSendMessageReq req = new CheckSendMessageReq();
        req.setAppId(msg.getMessageHeader().getAppId());
        req.setCommand(msg.getMessageHeader().getCommand());
        JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()));
        String fromId = jsonObject.getString("fromId");
        String groupId = jsonObject.getString("group");
        req.setFromId(fromId);
        req.setToId(groupId);

        // 1.调用业务层校验消息发送方的内部接口
        ResponseVO responseVO = feignMessageService.checkSendMessage(req);
        if (responseVO.isOk()) {
            // 2. 如果成功就投递到 MQ
            MqMessageProducer.sendMessage(msg, req.getCommand());
        } else {
            // 3. 如果失败就发送 ACK 失败响应报文
            ChatMessageAck chatMessageAck = new ChatMessageAck(jsonObject.getString("messageId"));
            responseVO.setData(chatMessageAck);
            MessagePack<ResponseVO> ack = new MessagePack<>();
            ack.setData(responseVO);
            ack.setCommand(GroupEventCommand.GROUP_MSG_ACK.getCommand());
            ctx.channel().writeAndFlush(ack);
        }

    }
}


