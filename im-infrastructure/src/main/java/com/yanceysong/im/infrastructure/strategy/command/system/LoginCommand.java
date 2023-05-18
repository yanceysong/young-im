package com.yanceysong.im.infrastructure.strategy.command.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.yanceysong.im.codec.pack.command.LoginPack;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.constant.ChannelConstants;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.enums.device.ConnectState;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.common.model.UserSession;
import com.yanceysong.im.infrastructure.redis.RedisManager;
import com.yanceysong.im.infrastructure.strategy.command.BaseCommandStrategy;
import com.yanceysong.im.infrastructure.strategy.command.model.CommandExecutionRequest;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName LoginCommand
 * @Description
 * @date 2023/4/25 10:25
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class LoginCommand extends BaseCommandStrategy {
    @Override
    public void systemStrategy(CommandExecutionRequest commandExecutionRequest) {
        ChannelHandlerContext ctx = commandExecutionRequest.getCtx();
        Message msg = commandExecutionRequest.getMsg();
        Integer brokeId = commandExecutionRequest.getBrokeId();
        // 解析 msg
        LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()),
                new TypeReference<LoginPack>() {

                }.getType());
        //解析到msg组装UserDTO
        UserClientDto userClientDto = new UserClientDto();
        userClientDto.setUserId(loginPack.getUserId());
        userClientDto.setAppId(msg.getMessageHeader().getAppId());
        userClientDto.setClientType(msg.getMessageHeader().getClientType());
        userClientDto.setImei(msg.getMessageHeader().getImei());

        // channel 设置属性
        ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).set(userClientDto.getUserId());
        ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.APP_ID)).set(userClientDto.getAppId());
        ctx.channel().attr(AttributeKey.valueOf(ChannelConstants.CLIENT_TYPE)).set(userClientDto.getClientType());

        // 双向绑定
        UserChannelRepository.bind(userClientDto, ctx.channel());

        // Redisson 高速存储用户 Session
        UserSession userSession = new UserSession();
        userSession.setUserId(loginPack.getUserId());
        userSession.setAppId(msg.getMessageHeader().getAppId());
        userSession.setClientType(msg.getMessageHeader().getClientType());
        userSession.setConnectState(ConnectState.CONNECT_STATE_OFFLINE.getCode());
        userSession.setBrokerId(brokeId);
        userSession.setImei(userClientDto.getImei());
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            userSession.setBrokerHost(localHost.getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        // 存储到 Redis
        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<String, String> map = redissonClient
                .getMap(msg.getMessageHeader().getAppId()
                        + RedisConstants.USER_SESSION_CONSTANTS
                        + loginPack.getUserId());
        map.put(msg.getMessageHeader().getClientType() + "", JSONObject.toJSONString(userSession));
    }
}
