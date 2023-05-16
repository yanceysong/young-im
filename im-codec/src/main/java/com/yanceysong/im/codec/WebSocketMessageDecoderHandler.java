package com.yanceysong.im.codec;

import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.codec.util.ByteBufToMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName WebSocketMessageDecoderHandler
 * @Description WebSocket消息处理器
 * @date 2023/5/15 10:18
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class WebSocketMessageDecoderHandler extends MessageToMessageDecoder<BinaryWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryWebSocketFrame msg, List<Object> out) throws Exception {
        ByteBuf content = msg.content();
        if (content.readableBytes() < Message.MESSAGE_MIN_SIZE) {
            return;
        }
        Message message = ByteBufToMessageUtils.transition(content);
        if (message == null) {
            return;
        }
        out.add(message);
    }
}
