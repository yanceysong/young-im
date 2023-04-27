package com.yanceysong.im.infrastructure.strategy.login.impl;

import com.yanceysong.im.common.enums.ClientType;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.login.LoginStatus;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import io.netty.channel.Channel;

import java.util.List;

/**
 * @ClassName ThreeClientLoginStatus
 * @Description
 * @date 2023/4/27 13:56
 * @Author yanceysong
 * @Version 1.0
 */
public class ThreeClientLoginStatus extends LoginStatus {
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

            boolean isSameClient = false;
            // 判断是否为同一类型客户端
            if (ClientType.isSameClient(dto.getClientType(), channelClientType)) {
                isSameClient = true;
            }

            if (isSameClient) {
                sendMutualLoginMsg(userChannel, channelClientType, channelImei, dto);
            }
        }
    }
}
