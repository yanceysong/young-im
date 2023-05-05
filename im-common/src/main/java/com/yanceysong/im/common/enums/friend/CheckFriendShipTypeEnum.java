package com.yanceysong.im.common.enums.friend;

/**
 * @ClassName CheckFriendShipTypeEnum
 * @Description
 * @date 2023/4/28 10:54
 * @Author yanceysong
 * @Version 1.0
 */
public enum CheckFriendShipTypeEnum {
    /**
     * 1 单方校验；2双方校验。
     */
    SINGLE(1),

    BOTH(2),
    ;

    private final int type;

    CheckFriendShipTypeEnum(int type){
        this.type=type;
    }

    public int getType() {
        return type;
    }
}
