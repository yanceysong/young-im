package com.yanceysong.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yanceysong.im.codec.pack.LoginPack;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.enums.command.ImSystemCommand;
import com.yanceysong.im.common.enums.connect.ImSystemConnectState;
import com.yanceysong.im.common.model.UserSession;
import com.yanceysong.im.tcp.redis.RedisManager;
import com.yanceysong.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName NettyServerHandler
 * @Description
 * @date 2023/4/25 9:54
 * @Author yanceysong
 * @Version 1.0
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private final static Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Integer command = msg.getMessageHeader().getCommand();
        // 登录指令 command
        if (command.equals(ImSystemCommand.COMMAND_LOGIN.getCode())) {
            // 解析 msg
            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()),
                    new TypeReference<LoginPack>() {

                    }.getType());
            // channel 设置属性
            ctx.channel().attr(AttributeKey.valueOf("userId")).set(loginPack.getUserId());
            // 将 channel 存起来
            SessionSocketHolder.put(loginPack.getUserId(), (NioSocketChannel) ctx.channel());

            // Redission 高速存储用户 Session
            UserSession userSession = new UserSession();
            userSession.setUserId(loginPack.getUserId());
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setConnectState(ImSystemConnectState.CONNECT_STATE_OFFLINE.getCode());
            // 存储到 Redis
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(
                    msg.getMessageHeader().getAppId() +
                            Constants.RedisConstants.UserSessionConstants +
                            loginPack.getUserId());
            map.put(msg.getMessageHeader().getClientType() + "",
                    JSONObject.toJSONString(userSession));
        }
    }

}
