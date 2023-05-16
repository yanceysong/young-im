package com.yanceysong.im.common.enums.group;

/**
 * @ClassName GroupMemberRoleEnum
 * @Description
 * @date 2023/4/28 10:57
 * @Author yanceysong
 * @Version 1.0
 */
public enum GroupMemberRoleEnum {
    /**
     * 普通成员
     */
    ORDINARY(0),

    /**
     * 管理员
     */
    MANAGER(1),

    /**
     * 群主
     */
    OWNER(2),

    /**
     * 离开
     */
    LEAVE(3);
    ;


    private final Integer code;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     * @param ordinal
     * @return
     */
    public static GroupMemberRoleEnum getItem(int ordinal) {
        for (int i = 0; i < GroupMemberRoleEnum.values().length; i++) {
            if (GroupMemberRoleEnum.values()[i].getCode() == ordinal) {
                return GroupMemberRoleEnum.values()[i];
            }
        }
        return null;
    }

    GroupMemberRoleEnum(Integer code){
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }
}
