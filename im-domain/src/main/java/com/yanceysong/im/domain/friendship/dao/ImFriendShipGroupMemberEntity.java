package com.yanceysong.im.domain.friendship.dao;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName ImFriendShipGroupMemberEntity
 * @Description
 * @date 2023/5/5 10:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@TableName("im_friendship_group_member")
public class ImFriendShipGroupMemberEntity {

    @TableId(value = "group_id")
    private Long groupId;

    private String toId;

}