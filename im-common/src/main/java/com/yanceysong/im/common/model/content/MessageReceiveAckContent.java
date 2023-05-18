package com.yanceysong.im.common.model.content;

import com.yanceysong.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName MessageReceiveAckPack
 * @Description 消息确认收到 ACK
 * @date 2023/5/17 11:14
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(doNotUseGetters = true)
public class MessageReceiveAckContent extends ClientInfo {

    /** 消息唯一标识 */
    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    /** 是否为服务端发送的消息 */
    private boolean serverSend = false;

}
