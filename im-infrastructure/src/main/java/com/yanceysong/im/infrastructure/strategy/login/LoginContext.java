package com.yanceysong.im.infrastructure.strategy.login;

import com.yanceysong.im.common.model.user.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.login.impl.AllClientLoginStatus;

/**
 * @ClassName LoginContext
 * @Description
 * @date 2023/4/27 13:51
 * @Author yanceysong
 * @Version 1.0
 */
public class LoginContext {
    private LoginStatus status;

    public LoginContext() {
        status = new AllClientLoginStatus();
        status.setContext(this);
    }

    public void setStatus(LoginStatus status) {
        this.status = status;
        this.status.setContext(this);
    }

    public void handleUserLogin(UserClientDto dto) {
        status.handleUserLogin(dto);
    }
}
