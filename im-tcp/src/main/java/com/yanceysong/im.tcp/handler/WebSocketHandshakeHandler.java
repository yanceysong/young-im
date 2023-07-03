package com.yanceysong.im.tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

import java.util.List;
import java.util.Map;

/**
 * @ClassName WebSocketHandshakeHandler
 * @Description
 * @date 2023/7/2 11:21
 * @Author yanceysong
 * @Version 1.0
 */
public class WebSocketHandshakeHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        // 验证握手请求
        // 判断请求是否是WebSocket握手请求
        if (isWebSocketUpgrade(request)) {
            // 自定义验证逻辑，例如校验请求头字段或使用令牌进行验证
            // 获取URL中的参数
            QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
            Map<String, List<String>> params = queryStringDecoder.parameters();
            List<String> identity = params.get("identity");
            if (!dealIdentity(identity)) {
                close(ctx, request);
            }
            // 验证成功，进行握手处理
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://server-uri", null, false);
            WebSocketServerHandshaker handShaker = factory.newHandshaker(request);
            if (handShaker == null) {
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                handShaker.handshake(ctx.channel(), request);
            }
        } else {
            close(ctx, request);
        }
    }

    private void close(ChannelHandlerContext ctx, FullHttpRequest request) {
        // 非WebSocket握手请求，视为非法连接，关闭连接
        request.release();
        ctx.channel().close();
    }

    private boolean dealIdentity(List<String> param) {
        //todo
        return true;
    }

    private boolean isWebSocketUpgrade(HttpRequest request) {
        HttpHeaders headers = request.headers();
        return headers.contains("Upgrade") && "websocket".equalsIgnoreCase(headers.get("Upgrade"));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 异常处理
        cause.printStackTrace();
        ctx.channel().close();
    }

}
