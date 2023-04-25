package com.yanceysong.im.infrastructure.strategy.utils;

import com.yanceysong.im.common.model.UserClientDto;

/**
 * @ClassName HostHolder
 * @Description
 * @date 2023/4/25 14:47
 * @Author yanceysong
 * @Version 1.0
 */
public class HostHolder {
    /**
     * 使用 ThreadLocal 存储用户信息
     */
    private static final ThreadLocal<UserClientDto> USERS = new ThreadLocal<>();

    public void setUser(UserClientDto user) {
        USERS.set(user);
    }

    public UserClientDto getUser() {
        return USERS.get();
    }

    public void clear() {
        USERS.remove();
    }
}
