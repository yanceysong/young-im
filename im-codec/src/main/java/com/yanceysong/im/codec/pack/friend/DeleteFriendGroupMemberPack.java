package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

import java.util.List;

/**
 * @ClassName DeleteFriendGroupMemberPack
 * @Description 删除好友分组成员通知报文
 * @date 2023/5/12 13:38
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DeleteFriendGroupMemberPack {

    public String fromId;

    private String groupName;

    private List<String> toIds;

    /** 序列号*/
    private Long sequence;

}