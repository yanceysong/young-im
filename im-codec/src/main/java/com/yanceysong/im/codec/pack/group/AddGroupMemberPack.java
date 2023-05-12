package com.yanceysong.im.codec.pack.group;

import lombok.Data;

import java.util.List;

/**
 * @ClassName AddGroupMemberPack
 * @Description 群内添加群成员通知报文
 * @date 2023/5/12 13:40
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddGroupMemberPack {

    private String groupId;

    private List<String> members;

}
