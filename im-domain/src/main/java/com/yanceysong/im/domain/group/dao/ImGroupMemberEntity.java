package com.yanceysong.im.domain.group.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName ImGroupMemberEntity
 * @Description
 * @date 2023/5/5 11:47
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@TableName("im_group_member")
@ToString(doNotUseGetters=true)
@EqualsAndHashCode(callSuper= false)
public class ImGroupMemberEntity {

    @TableId(type = IdType.AUTO)
    private Long groupMemberId;

    private Integer appId;

    private String groupId;

    /**
     * 成员id
     */
    private String memberId;

    /**
     * 群成员类型，0 普通成员, 1 管理员, 2 群主， 3 禁言，4 已经移除的成员
     */
    private Integer role;

    private Long speakDate;

    /**
     * 群昵称
     */
    private String alias;

    /**
     * 加入时间
     */
    private Long joinTime;

    /**
     * 离开时间
     */
    private Long leaveTime;

    private String joinType;

    private String extra;
}
