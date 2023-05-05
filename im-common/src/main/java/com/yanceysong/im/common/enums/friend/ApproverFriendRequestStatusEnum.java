package com.yanceysong.im.common.enums.friend;

/**
 * @ClassName ApproverFriendRequestStatusEnum
 * @Description
 * @date 2023/4/28 10:54
 * @Author yanceysong
 * @Version 1.0
 */
public enum ApproverFriendRequestStatusEnum {
    /**
     * 1 同意；2 拒绝。
     */
    AGREE(1),

    REJECT(2),
    ;

    private final int code;

    ApproverFriendRequestStatusEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
