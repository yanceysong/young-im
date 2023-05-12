package com.yanceysong.im.codec.pack.group;

import lombok.Data;

/**
 * @ClassName UpdateGroupMemberPack
 * @Description 修改群成员通知报文
 * @date 2023/5/12 13:42
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UpdateGroupMemberPack {

    private String groupId;

    private String memberId;

    private String alias;

    private String extra;

}