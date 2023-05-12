package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

/**
 * @ClassName UpdateFriendPack
 * @Description 修改好友通知报文
 * @date 2023/5/12 13:39
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UpdateFriendPack {
    public String fromId;

    private String toId;

    private String remark;

    private Long sequence;
}
