package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

/**
 * @ClassName ApproverFriendRequestPack
 * @Description 审批好友申请通知报文
 * @date 2023/5/12 13:37
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class ApproverFriendRequestPack {
    private Long id;

    //1同意 2拒绝
    private Integer status;

    private Long sequence;

}
