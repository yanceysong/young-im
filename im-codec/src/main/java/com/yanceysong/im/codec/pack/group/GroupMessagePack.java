package com.yanceysong.im.codec.pack.group;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName GroupMessagePack
 * @Description
 * @date 2023/5/12 13:41
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class GroupMessagePack {

    //客户端传的messageId
    private String messageId;

    private String messageKey;

    private String sendId;

    private String groupId;

    private int messageRandom;

    private long messageTime;

    private long messageSequence;

    private String messageBody;
    /**
     * 这个字段缺省或者为 0 表示需要计数，为 1 表示本条消息不需要计数，即右上角图标数字不增加
     */
    private int badgeMode;

    private Long messageLifeTime;

    private Integer appId;

}
