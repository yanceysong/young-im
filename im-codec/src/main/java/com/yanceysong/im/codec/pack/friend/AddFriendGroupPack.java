package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

/**
 * @ClassName AddFriendGroupPack
 * @Description 用户创建好友分组通知包
 * @date 2023/5/12 13:36
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddFriendGroupPack {
    public String fromId;

    private String groupName;

    /** 序列号*/
    private Long sequence;
}
