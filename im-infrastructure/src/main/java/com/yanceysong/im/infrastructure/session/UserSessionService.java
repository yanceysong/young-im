package com.yanceysong.im.infrastructure.session;

import com.yanceysong.im.common.model.user.UserSession;

import java.util.List;

/**
 * @ClassName UserSessionService
 * @Description 定义获取 UserSession 的接口
 * @date 2023/5/12 10:57
 * @Author yanceysong
 * @Version 1.0
 */
public interface UserSessionService {

    /**
     * 获取用户所有的 Session 信息
     *
     * @param appId  app的id
     * @param userId 用户id
     * @return 用户所有客户端的session
     */
    List<UserSession> getUserSession(Integer appId, String userId);

    /**
     * 获取指定端的用户 Session 信息
     *
     * @param appId      app的id
     * @param userId     用户id
     * @param clientType 指定的客户端类型
     * @param imei       imei
     * @return 指定客户端的Session
     */
    UserSession getUserSession(Integer appId, String userId, Integer clientType, String imei);

}