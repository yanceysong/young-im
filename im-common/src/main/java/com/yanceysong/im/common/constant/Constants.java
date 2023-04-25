package com.yanceysong.im.common.constant;

/**
 * @ClassName Constants
 * @Description
 * @date 2023/4/24 16:34
 * @Author yanceysong
 * @Version 1.0
 */
public class Constants {
    public static class ChannelConstants {
        /**
         * channel 绑定的 userId Key
         */
        public static final String UserId = "userId";
        /**
         * channel 绑定的 appId Key
         */
        public static final String AppId = "appId";
        /**
         * channel 绑定的端类型
         */
        public static final String ClientType = "clientType";
        /**
         * channel 绑定的读写时间
         */
        public static final String ReadTime = "readTime";
    }
    public static class RedisConstants {
        /**
         * 用户session：格式为 appId + userSessionConstants + 用户 ID
         * 例如：10001:userSessionConstants:userId
         */
        public static final String UserSessionConstants = ":userSession:";
    }
}
