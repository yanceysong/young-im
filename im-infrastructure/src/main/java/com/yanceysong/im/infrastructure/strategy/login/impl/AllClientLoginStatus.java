package com.yanceysong.im.infrastructure.strategy.login.impl;

import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.login.LoginStatus;

/**
 * @ClassName AllClientLoginStatus
 * @Description 允许所有客户端在线
 * @date 2023/4/27 13:55
 * @Author yanceysong
 * @Version 1.0
 */
public class AllClientLoginStatus extends LoginStatus {
    @Override
    public void switchStatus(LoginStatus status) {
        context.setStatus(status);
    }

    @Override
    public void handleUserLogin(UserClientDto dto) {
        // 放权，允许多设备登录，同端之间也不做逻辑处理
    }
}
