package com.yanceysong.im.common.model.read;

import com.yanceysong.im.common.model.common.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName MessageReadContent
 * @Description 消息已读数据包
 * @date 2023/5/17 13:35
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(doNotUseGetters = true)
public class MessageReadContent extends ClientInfo {

    /** 消息已读偏序 */
    private long messageSequence;
    private String messageId;

    /** 要么 sendId + receiverId */
    private String sendId;
    private String receiverId;

    /** 要么 groupId */
    private String groupId;

    /** 标识消息来源于单聊还是群聊 */
    private Integer conversationType;

}