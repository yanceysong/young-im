package com.yanceysong.im.codec.pack.message;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName MessageReceiveServerAckPack
 * @Description
 * @date 2023/5/17 13:27
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@ToString(doNotUseGetters = true)
public class MessageReceiveServerAckPack {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    private Boolean serverSend;
}
