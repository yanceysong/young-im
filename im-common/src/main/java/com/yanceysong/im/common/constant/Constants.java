package com.yanceysong.im.common.constant;

/**
 * @ClassName Constants
 * @Description
 * @date 2023/4/24 16:34
 * @Author yanceysong
 * @Version 1.0
 */
public class Constants {
    public static class RedisConstants {
        /**
         * 用户session：格式为 appId + userSessionConstants + 用户 ID
         * 例如：10001:userSessionConstants:userId
         */
        public static final String UserSessionConstants = ":userSession:";
    }
}
