package com.yanceysong.im.codec.pack.friend;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName AddFriendPack
 * @Description 添加好友通知报文
 * @date 2023/5/12 13:36
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class AddFriendPack {
    private String sendId;

    /**
     * 备注
     */
    private String remark;
    private String receiverId;
    /**
     * 好友来源
     */
    private String addSource;
    /**
     * 添加好友时的描述信息（用于打招呼）
     */
    private String addWording;

    private Long sequence;
}
