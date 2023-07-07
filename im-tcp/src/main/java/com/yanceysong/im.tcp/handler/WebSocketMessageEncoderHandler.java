package com.yanceysong.im.tcp.handler;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.proto.MessagePack;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName WebSocketMessageEncoderHandler
 * @Description
 * @date 2023/5/15 10:22
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class WebSocketMessageEncoderHandler extends MessageToMessageEncoder<MessagePack> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MessagePack msg, List<Object> out)  {
        try {
            String s = JSONObject.toJSONString(msg);
            ByteBuf byteBuf = Unpooled.directBuffer(8+s.length());
            byte[] bytes = s.getBytes();
            byteBuf.writeInt(msg.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(new BinaryWebSocketFrame(byteBuf));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}