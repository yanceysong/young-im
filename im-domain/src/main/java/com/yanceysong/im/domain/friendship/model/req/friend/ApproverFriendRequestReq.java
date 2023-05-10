package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

/**
 * @ClassName ApproverFriendRequestReq
 * @Description
 * @date 2023/5/5 10:57
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class ApproverFriendRequestReq extends RequestBase {

    private Long id;

    //1同意 2拒绝
    private Integer status;
}