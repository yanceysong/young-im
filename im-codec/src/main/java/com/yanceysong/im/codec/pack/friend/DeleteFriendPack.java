package com.yanceysong.im.codec.pack.friend;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName DeleteFriendPack
 * @Description 删除好友通知报文
 * @date 2023/5/12 13:38
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class DeleteFriendPack {

    private String sendId;

    private String receiverId;

    private Long sequence;

}