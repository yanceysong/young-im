package com.yanceysong.im.common.model.read;

import com.yanceysong.im.common.model.ClientInfo;
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

    /** 要么 fromId + toId */
    private String fromId;
    private String toId;

    /** 要么 groupId */
    private String groupId;

    /** 标识消息来源于单聊还是群聊 */
    private Integer conversationType;

}