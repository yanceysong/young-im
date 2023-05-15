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
        public static final String USER_ID = "userId";
        /**
         * channel 绑定的 appId Key
         */
        public static final String APP_ID = "appId";
        /**
         * channel 绑定的端类型
         */
        public static final String CLIENT_TYPE = "clientType";
        /**
         * channel 绑定的读写时间
         */
        public static final String READ_TIME = "readTime";
        /**
         * channel 绑定的imei 号，标识用户登录设备号
         */
        public static final String IMEI = "imei";
        /**
         * redisson存的userChannel的key
         */
        public static final String USER_CHANNEL_KEY = APP_ID + ":" + USER_ID + ":" + CLIENT_TYPE;
        /**
         * channel 绑定的 clientType 和 imei Key
         */
        public static final String CLIENT_IMEI = "clientImei";

    }

    public static class RedisConstants {
        /**
         * UserSign，格式：appId:UserSign:
         */
        public static final String USER_SIGN = ":UserSign:";

        /**
         * 用户登录端消息通道信息
         */
        public static final String USER_LOGIN_CHANNEL = "signal/channel/LOGIN_USER_INNER_QUEUE";
        /**
         * 用户session：格式为 appId + userSessionConstants + 用户 ID
         * 例如：10001:userSessionConstants:userId
         */
        public static final String USER_SESSION_CONSTANTS = ":userSession:";
    }

    public static class RabbitmqConstants {

        public static final String IM2_USER_SERVICE = "pipeline2UserService";

        public static final String IM2_MESSAGE_SERVICE = "pipeline2MessageService";

        public static final String IM2_GROUP_SERVICE = "pipeline2GroupService";

        public static final String IM2_FRIENDSHIP_SERVICE = "pipeline2FriendshipService";

        public static final String MESSAGE_SERVICE2_IM = "messageService2Pipeline";

        public static final String GROUP_SERVICE2_IM = "GroupService2Pipeline";

        public static final String FRIEND_SHIP2_IM = "friendShip2Pipeline";

        public static final String STORE_P2P_MESSAGE = "storeP2PMessage";

        public static final String STORE_GROUP_MESSAGE = "storeGroupMessage";
    }

    public static class ZkConstants {

        public static final String IM_CORE_ZK_ROOT = "/im-coreRoot";

        public static final String IM_CORE_ZK_ROOT_TCP = "/tcp";

        public static final String IM_CORE_ZK_ROOT_WEB = "/web";
    }
    public static class CallbackCommand{

        public static final String MODIFY_USER_AFTER = "user.modify.after";

        public static final String CREATE_GROUP_AFTER = "group.create.after";

        public static final String UPDATE_GROUP_AFTER = "group.update.after";

        public static final String DESTROY_GROUP_AFTER = "group.destroy.after";

        public static final String TRANSFER_GROUP_AFTER = "group.transfer.after";

        public static final String GROUP_MEMBER_ADD_BEFORE = "group.member.add.before";

        public static final String GROUP_MEMBER_ADD_AFTER = "group.member.add.after";

        public static final String GROUP_MEMBER_DELETE_AFTER = "group.member.delete.after";

        public static final String ADD_FRIEND_BEFORE = "friend.add.before";

        public static final String ADD_FRIEND_AFTER = "friend.add.after";

        public static final String UPDATE_FRIEND_BEFORE = "friend.update.before";

        public static final String UPDATE_FRIEND_AFTER = "friend.update.after";

        public static final String DELETE_FRIEND_AFTER = "friend.delete.after";

        public static final String ADD_BLACK_AFTER = "black.add.after";

        public static final String DELETE_BLACK = "black.delete";

        public static final String SEND_MESSAGE_AFTER = "message.send.after";

        public static final String SEND_MESSAGE_BEFORE = "message.send.before";

    }

}
