package com.yanceysong.im.codec.pack.friend;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName ReadAllFriendRequestPack
 * @Description 已读好友申请通知报文
 * @date 2023/5/12 13:39
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class ReadAllFriendRequestPack {
    private String fromId;

    private Long sequence;
}
