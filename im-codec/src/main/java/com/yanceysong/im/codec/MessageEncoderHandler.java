package com.yanceysong.im.codec;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.proto.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName MessageEncoderHandler
 * @Description 消息编码类，私有协议规则，前4位表示长度，接着command4位，后面是数据
 * 服务端向客户端发送数据需要
 * @date 2023/4/27 10:13
 * @Author yanceysong
 * @Version 1.0
 */
public class MessageEncoderHandler extends MessageToByteEncoder {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof MessagePack) {
            MessagePack msgBody = (MessagePack) msg;
            String s = JSONObject.toJSONString(msgBody.getData());
            byte[] bytes = s.getBytes();
            out.writeInt(msgBody.getCommand());
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
