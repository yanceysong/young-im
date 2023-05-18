package com.yanceysong.im.common.enums.conversation;

/**
 * @ClassName ConversationTypeEnum
 * @Description
 * @date 2023/5/17 13:30
 * @Author yanceysong
 * @Version 1.0
 */
public enum ConversationTypeEnum {

    /**
     * 0 单聊 1群聊 2机器人 3公众号
     */
    P2P(0),

    GROUP(1),

    ROBOT(2),
    OFFICIAL(3);

    private final Integer code;

    ConversationTypeEnum(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}

