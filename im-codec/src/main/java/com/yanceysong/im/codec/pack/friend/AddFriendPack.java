package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

/**
 * @ClassName AddFriendPack
 * @Description 添加好友通知报文
 * @date 2023/5/12 13:36
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddFriendPack {
    private String fromId;

    /**
     * 备注
     */
    private String remark;
    private String toId;
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
