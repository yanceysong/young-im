package com.yanceysong.im.common.model.content;

import com.yanceysong.im.common.constant.CodeAdapter;

/**
 * @ClassName ImSystemMessageType
 * @Description
 * @date 2023/4/24 16:39
 * @Author yanceysong
 * @Version 1.0
 */
public enum MessageType implements CodeAdapter {
    // 0x0. json、 0x1. protobuf、 0x2. xml
    DATA_TYPE_JSON(0x0),
    DATA_TYPE_PROTOBUF(0x1),
    DATA_TYPE_XML(0x2);

    private final Integer msgType;

    MessageType(Integer msgType) {
        this.msgType = msgType;
    }

    @Override
    public Integer getCode() {
        return msgType;
    }
}
