package com.yanceysong.im.infrastructure.strategy.login.impl;

import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.login.LoginStatus;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @ClassName OneClientLoginStatus
 * @Description
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
        List<Channel> userChannels = UserChannelRepository.getUserChannels(dto.getAppId(), dto.getUserId());
        for (Channel userChannel : userChannels) {
            UserClientDto userInfo = UserChannelRepository.getUserInfo(userChannel);
            Integer channelClientType = userInfo.getClientType();
            String channelImei = userInfo.getImei();

            // 单端登录直接向 channel 旧端发送下线通知
            sendMutualLoginMsg(userChannel, channelClientType, channelImei, dto);
        }
    }
}
