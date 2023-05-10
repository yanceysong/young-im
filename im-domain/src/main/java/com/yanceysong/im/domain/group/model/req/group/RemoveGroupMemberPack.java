package com.yanceysong.im.domain.group.model.req.group;

import lombok.Data;

/**
 * @ClassName RemoveGroupMemberPack
 * @Description 踢人出群通知报文
 * @date 2023/5/10 9:49
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class RemoveGroupMemberPack {

    private String groupId;

    private String member;

}
