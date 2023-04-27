package com.yanceysong.im.infrastructure.strategy.login.impl;

import com.yanceysong.im.common.enums.ClientType;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.login.LoginStatus;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @ClassName TwoClientLoginStatus
 * @Description
 * @date 2023/4/27 13:57
 * @Author yanceysong
 * @Version 1.0
 */
public class TwoClientLoginStatus extends LoginStatus {
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

            // 允许 Web 多端登录
            if (ClientType.WEB.getCode().equals(dto.getClientType()) || ClientType.WEB.getCode().equals(channelClientType)) {
                continue;
            }

            sendMutualLoginMsg(userChannel, channelClientType, channelImei, dto);
        }
    }
}
