package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

/**
 * @ClassName DeleteFriendPack
 * @Description 删除好友通知报文
 * @date 2023/5/12 13:38
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DeleteFriendPack {

    private String fromId;

    private String toId;

    private Long sequence;

}