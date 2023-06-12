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
    public static final String CACHE_MESSAGE = ":cacheMessage:";
    /**
     * 缓存离线消息 获取用户消息队列 格式：appId + :offlineMessage: + sendId / receiverId
     */
    public static final String OFFLINE_MESSAGE = ":offlineMessage:";
    /**
     * 缓存群组成员列表
     */
    public static final String GROUP_MEMBERS = ":groupMembers:";
    /**
     * 用户所有模块的偏序前缀
     */
    public static final String SEQ_PRE_FIX = ":seq:";
    /**
     * 用户订阅列表，格式 ：appId + :subscribe: + userId。Hash结构，filed为订阅自己的人
     */
    public static final String SUBSCRIBE = "subscribe";
    /**
     * 用户自定义在线状态，格式 ：appId + :userCustomerStatus: + userId。set，value为用户id
     */
    public static final String USER_CUSTOMER_STATUS= "userCustomerStatus";


}
