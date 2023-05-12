package com.yanceysong.im.codec.pack.user;

import lombok.Data;

/**
 * @ClassName UserModifyPack
 * @Description
 * @date 2023/5/12 13:42
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UserModifyPack {

    // 用户id
    private String userId;

    // 用户名称
    private String nickName;

    private String password;

    // 头像
    private String photo;

    // 性别
    private String userSex;

    // 个性签名
    private String selfSignature;

    // 加好友验证类型（Friend_AllowType） 1需要验证
    private Integer friendAllowType;

}
