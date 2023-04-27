package com.yanceysong.im.infrastructure.utils;

import com.yanceysong.im.common.model.UserClientDto;

/**
 * @ClassName HostHolder
 * @Description
 * @date 2023/4/27 13:59
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
