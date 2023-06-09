package com.yanceysong.im.codec.pack.friend;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName AddFriendGroupMemberPack
 * @Description 好友分组添加成员通知包
 * @date 2023/5/12 13:35
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class AddFriendGroupMemberPack {

    public String sendId;

    private String groupName;

    private List<String> receiverIds;

    /** 序列号*/
    private Long sequence;

}
