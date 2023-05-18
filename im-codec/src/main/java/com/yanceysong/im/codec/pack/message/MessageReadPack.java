package com.yanceysong.im.codec.pack.message;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName MessageReadPack
 * @Description
 * @date 2023/5/17 13:27
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@ToString(doNotUseGetters = true)
public class MessageReadPack {

    /** 消息已读偏序 */
    private long messageSequence;

    /** 要么 fromId + toId */
    private String fromId;
    private String toId;

    /** 要么 groupId */
    private String groupId;

    /** 标识消息来源于单聊还是群聊 */
    private Integer conversationType;

}
