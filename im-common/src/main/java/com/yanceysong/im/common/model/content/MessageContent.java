package com.yanceysong.im.common.model.content;

import com.yanceysong.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName MessageContent
 * @Description
 * @date 2023/5/16 9:56
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class MessageContent extends ClientInfo {
    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

    private String extra;

    private Long messageTime;

    private Long messageKey;
    private Long messageSequence;
}
