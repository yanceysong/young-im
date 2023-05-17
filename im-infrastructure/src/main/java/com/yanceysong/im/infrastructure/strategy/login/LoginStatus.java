package com.yanceysong.im.infrastructure.strategy.login;

import com.yanceysong.im.codec.proto.MessagePack;
import com.yanceysong.im.common.constant.ChannelConstants;
import com.yanceysong.im.common.enums.device.ClientType;
import com.yanceysong.im.common.enums.command.SystemCommand;
import com.yanceysong.im.common.model.UserClientDto;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName LoginStates
 * @Description
 * @date 2023/4/27 13:51
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public abstract class LoginStatus {
    protected static Map<Integer, String> map = new ConcurrentHashMap<>();

    static {
        //枚举类信息转为map
        for (ClientType c : ClientType.values()) {
            map.put(c.getCode(), c.getInfo());
        }

//        map.put(ClientType.ANDROID.getCode(), ClientType.ANDROID.getInfo());
//        map.put(ClientType.WEB.getCode(), ClientType.WEB.getInfo());
//        map.put(ClientType.IOS.getCode(), ClientType.IOS.getInfo());
//        map.put(ClientType.WINDOWS.getCode(), ClientType.WINDOWS.getInfo());
//        map.put(ClientType.MAC.getCode(), ClientType.MAC.getInfo());
//        map.put(ClientType.WEBAPI.getCode(), ClientType.WEBAPI.getInfo());
    }

    protected LoginContext context;


    /**
     * 发送用户下线消息
     * 并不是真正粗暴清除 channel 里的旧信息，因为需要等待数据包停止传输
     * 在服务器行为中，能清除 channel 里旧信息的方式只有 用户登出 Logout 和 心跳超时 Ping-out
     *
     * @param userChannel       用户的channel
     * @param channelClientType 客户端类型
     * @param channelImei       Imei
     * @param dto               用户信息
     */
    public static void sendMutualLoginMsg(Channel userChannel, Integer channelClientType, String channelImei, UserClientDto dto) {
        // 踢掉 channel 所绑定的旧的同端登录状态
        String channelDevice = parseClientType(channelClientType) + ":" + channelImei;
        String newChannelDevice = parseClientType(dto.getClientType()) + ":" + dto.getImei();
        if (!(channelDevice).equals(newChannelDevice)) {
            // 行为埋点
            log.info("第三方平台(appId) [{}] 用户(userId) [{}] 从新端 [{}] 登录(login) , 旧端 [{}] 下线(line) ",
                    dto.getAppId(), dto.getUserId(), newChannelDevice, channelDevice);
            MessagePack<Object> pack = new MessagePack<>();
            pack.setToId((String) userChannel.attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).get());
            pack.setUserId((String) userChannel.attr(AttributeKey.valueOf(ChannelConstants.USER_ID)).get());
            pack.setCommand(SystemCommand.MUTA_LOGIN.getCommand());
            userChannel.writeAndFlush(pack);
        }
    }

    /**
     * map当中存储了codeType与设备类型的映射
     *
     * @param clientType 类型
     * @return 设备类型
     */
    public static String parseClientType(Integer clientType) {
        return map.get(clientType);
    }

    public void setContext(LoginContext context) {
        this.context = context;
    }

    public abstract void switchStatus(LoginStatus status);

    public abstract void handleUserLogin(UserClientDto dto);
}
