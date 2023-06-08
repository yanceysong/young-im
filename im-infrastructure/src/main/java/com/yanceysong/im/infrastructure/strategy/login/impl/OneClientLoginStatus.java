package com.yanceysong.im.infrastructure.strategy.login.impl;

import com.yanceysong.im.common.model.user.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.login.LoginStatus;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @ClassName OneClientLoginStatus
 * @Description 只允许一个客户端在线
 * @date 2023/4/27 13:56
 * @Author yanceysong
 * @Version 1.0
 */
public class OneClientLoginStatus extends LoginStatus {
    @Override
    public void switchStatus(LoginStatus status) {
        context.setStatus(status);
    }

    @Override
    public void handleUserLogin(UserClientDto dto) {
        //拿到这个用户的所有channel
        List<Channel> userChannels = UserChannelRepository.getUserChannels(dto.getAppId(), dto.getUserId());
        //遍历
        for (Channel userChannel : userChannels) {
            //因为channel与user是双向绑定的，通过channel获取到这个用户的信息
            UserClientDto userInfo = UserChannelRepository.getUserInfo(userChannel);
            //设备类型
            Integer channelClientType = userInfo.getClientType();
            String channelImei = userInfo.getImei();
            // 单端登录直接向 channel 旧端发送下线通知
            sendMutualLoginMsg(userChannel, channelClientType, channelImei, dto);
        }
    }
}
