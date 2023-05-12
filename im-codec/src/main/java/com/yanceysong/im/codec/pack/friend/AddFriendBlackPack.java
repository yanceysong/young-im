package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

/**
 * @ClassName AddFriendBlackPack
 * @Description 用户添加黑名单以后 tcp 通知数据包
 * @date 2023/5/12 13:35
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddFriendBlackPack {

    private String fromId;

    private String toId;

    private Long sequence;

}