package com.yanceysong.im.common.constant;

/**
 * @ClassName RedisConstants
 * @Description
 * @date 2023/5/17 10:22
 * @Author yanceysong
 * @Version 1.0
 */
public class RedisConstants {
    /**
     * UserSign，格式：appId:UserSign:
     */
    public static final String USER_SIGN = "userSign:";

    /**
     * 用户登录端消息通道信息
     */
    public static final String USER_LOGIN_CHANNEL = "signal/channel/LOGIN_USER_INNER_QUEUE";
    /**
     * 用户session：格式为 appId + userSessionConstants + 用户 ID
     * 例如：10001:userSessionConstants:userId
     */
    public static final String USER_SESSION_CONSTANTS = ":userSession:";
    /**
     * 缓存客户端消息防重，格式： appId + :cacheMessage: + messageId
     */
    public static final String CacheMessage = ":cacheMessage:";
    /**
     * 缓存离线消息 获取用户消息队列 格式：appId + :offlineMessage: + fromId / toId
     */
    public static final String OfflineMessage = ":offlineMessage:";
    /**
     * 缓存群组成员列表
     */
    public static final String GroupMembers = ":groupMembers:";


}
