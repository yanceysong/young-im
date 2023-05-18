package com.yanceysong.im.common.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName OfflineMessageContent
 * @Description
 * @date 2023/5/17 13:35
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class OfflineMessageContent {

    private Integer appId;

    /**
     * messageBodyId
     */
    private Long messageKey;

    /**
     * messageBody
     */
    private String messageBody;

    private Long messageTime;

    private String extra;

    private Integer delFlag;

    private String fromId;

    private String toId;

    /**
     * 序列号
     */
    private Long messageSequence;

    private String messageRandom;

    private Integer conversationType;

    private String conversationId;

}
