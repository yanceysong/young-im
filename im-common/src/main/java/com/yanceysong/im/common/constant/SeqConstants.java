package com.yanceysong.im.common.constant;

/**
 * @ClassName SeqConstants
 * @Description
 * @date 2023/5/17 11:22
 * @Author yanceysong
 * @Version 1.0
 */
public class SeqConstants {
    /**
     * 单聊消息有序
     */
    public static final String MESSAGE_SEQ = ":messageSeq:";
    /**
     * 群聊消息有序
     */
    public static final String GROUP_MESSAGE_SEQ = ":groupMessageSeq:";
    /**
     * 会话消息有序
     */
    public static final String CONVERSATION_SEQ = "conversationSeq";
    // 用于消息数据同步 Key
    /** 好友数量记录 */
    public static final String FRIEND_SHIP_SEQ = "friendShipSeq";
    /** 好友申请记录 */
    public static final String FRIEND_SHIP_REQUEST_SEQ = "friendShipRequestSeq";
    /** 好友标签记录 */
    public static final String FRIEND_SHIP_GROUP_SEQ = "friendShipGroupSeq";
    /** 群聊数量记录 */
    public static final String GROUP_SEQ = "groupSeq";


}
