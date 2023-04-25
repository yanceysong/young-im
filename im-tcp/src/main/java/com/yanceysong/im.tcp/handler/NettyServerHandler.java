package com.yanceysong.im.tcp.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yanceysong.im.codec.pack.LoginPack;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.enums.command.ImSystemCommand;
import com.yanceysong.im.common.enums.connect.ImSystemConnectState;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.common.model.UserSession;
import com.yanceysong.im.infrastructure.strategy.command.redis.RedisManager;
import com.yanceysong.im.infrastructure.strategy.utils.SessionSocketHolder;
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

    //    @Override
//    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
//        Integer command = parseCommand(msg);
//        CommandFactory commandFactory = new CommandFactory();
//        CommandStrategy commandStrategy = commandFactory.getCommandStrategy(command);
//        commandStrategy.doStrategy(ctx, msg);
//    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
        Integer command = msg.getMessageHeader().getCommand();
        //登录command
        if (ImSystemCommand.COMMAND_LOGIN.getCode().equals(command)) {

            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()),
                    new TypeReference<LoginPack>() {
                    }.getType());
            /** 登陸事件 **/
            String userId = loginPack.getUserId();
            /** 为channel设置用户id **/
            ctx.channel().attr(AttributeKey.valueOf(Constants.ChannelConstants.UserId)).set(userId);
            /** 为channel设置appId **/
            ctx.channel().attr(AttributeKey.valueOf(Constants.ChannelConstants.AppId)).set(msg.getMessageHeader().getAppId());
            /** 为channel设置ClientType **/
            ctx.channel().attr(AttributeKey.valueOf(Constants.ChannelConstants.ClientType))
                    .set(msg.getMessageHeader().getClientType());
            //Redis map
            UserSession userSession = new UserSession();
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setConnectState(ImSystemConnectState.CONNECT_STATE_ONLINE.getCode());

            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(msg.getMessageHeader().getAppId() + Constants.RedisConstants.UserSessionConstants + loginPack.getUserId());
            map.put(msg.getMessageHeader().getClientType() + ":" + msg.getMessageHeader().getImei()
                    , JSONObject.toJSONString(userSession));
            UserClientDto userClientDto = new UserClientDto();
            userClientDto.setUserId(loginPack.getUserId());
            userClientDto.setAppId(msg.getMessageHeader().getAppId());
            userClientDto.setClientType(msg.getMessageHeader().getClientType());
            SessionSocketHolder.put(userClientDto, (NioSocketChannel) ctx.channel());

        } else if (ImSystemCommand.COMMAND_LOGOUT.getCode().equals(command)) {
            //删除session
            //redis 删除
            SessionSocketHolder.removeUserSession((NioSocketChannel) ctx.channel());
        } else if (ImSystemCommand.COMMAND_PING.getCode().equals(command)) {
            //如果是ping的指令，那么就记录当前的时间
            ctx.channel()
                    .attr(AttributeKey.valueOf(Constants.ChannelConstants.ReadTime)).set(System.currentTimeMillis());
        }
    }

    protected Integer parseCommand(Message msg) {
        return msg.getMessageHeader().getCommand();
    }

}
