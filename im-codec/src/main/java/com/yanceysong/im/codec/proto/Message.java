package com.yanceysong.im.codec.proto;

import lombok.Data;

/**
 * @ClassName Message
 * @Description
 * @date 2023/4/24 16:30
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class Message {
    private MessageHeader messageHeader;

    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}
