package com.yanceysong.im.domain.group.model.req.group;
import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName CreateGroupReq
 * @Description
 * @date 2023/5/5 11:54
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class CreateGroupReq extends RequestBase {

    private String groupId;
    //群主id
    private String ownerId;

    //群类型 1私有群（类似微信） 2公开群(类似qq）
    private Integer groupType;

    private String groupName;

    private Integer mute;// 是否全员禁言，0 不禁言；1 全员禁言。

    private Integer applyJoinType;//加入群权限，0 所有人可以加入；1 群成员可以拉人；2 群管理员或群组可以拉人。

    private String introduction;//群简介

    private String notification;//群公告

    private String photo;//群头像

    private Integer MaxMemberCount;

    private List<GroupMemberDto> member;

    private String extra;

}