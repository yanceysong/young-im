package com.yanceysong.im.domain.message.model.req;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName SendMessageReq
 * @Description
 * @date 2023/5/16 10:37
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class SendMessageReq extends RequestBase {

    //客户端传的messageId
    private String messageId;

    private String fromId;

    private String toId;

    private int messageRandom;

    private long messageTime;

    private String messageBody;
    /**
     * 这个字段缺省或者为 0 表示需要计数，为 1 表示本条消息不需要计数，即右上角图标数字不增加
     */
    private int badgeMode;

    private Long messageLifeTime;

    private Integer appId;

}
