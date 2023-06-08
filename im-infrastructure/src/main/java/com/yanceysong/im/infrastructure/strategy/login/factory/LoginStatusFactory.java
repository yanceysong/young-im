package com.yanceysong.im.infrastructure.strategy.login.factory;

import com.yanceysong.im.common.model.user.UserClientDto;
import com.yanceysong.im.infrastructure.strategy.login.LoginContext;
import com.yanceysong.im.infrastructure.strategy.login.LoginStatus;

/**
 * @ClassName LoginStatusFactory
 * @Description
 * @date 2023/4/27 13:54
 * @Author yanceysong
 * @Version 1.0
 */
public class LoginStatusFactory extends LoginStatusFactoryConfig {

    private final LoginContext ctx = new LoginContext();

    /**
     * 上下文存储、路由用户所选择的端同步类型
     *
     * @param status 状态
     */
    public void chooseLoginStatus(Integer status) {
        LoginStatus loginStatus = LoginStatusMap.get(status);
        ctx.setStatus(loginStatus);
    }

    /**
     * 处理用户所选择端同步类型，判断是否需要下线 channel 旧信息
     *
     * @param dto 上下文
     */
    public void handleUserLogin(UserClientDto dto) {
        ctx.handleUserLogin(dto);
    }
}
