package com.yanceysong.im.common.model;

import com.yanceysong.im.common.model.common.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName RecallMessageContent
 * @Description
 * @date 2023/6/8 15:35
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= true)
@ToString(doNotUseGetters=true)
public class RecallMessageContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageTime;

    private Long messageSequence;

    private Integer conversationType;
}
