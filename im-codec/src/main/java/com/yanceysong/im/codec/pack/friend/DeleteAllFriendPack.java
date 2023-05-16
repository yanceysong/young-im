package com.yanceysong.im.codec.pack.friend;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName DeleteAllFriendPack
 * @Description 删除黑名单通知报文
 * @date 2023/5/12 13:37
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class DeleteAllFriendPack {
    private String fromId;
}
