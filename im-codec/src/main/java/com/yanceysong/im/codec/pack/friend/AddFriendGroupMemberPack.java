package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

import java.util.List;

/**
 * @ClassName AddFriendGroupMemberPack
 * @Description 好友分组添加成员通知包
 * @date 2023/5/12 13:35
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddFriendGroupMemberPack {

    public String fromId;

    private String groupName;

    private List<String> toIds;

    /** 序列号*/
    private Long sequence;

}
