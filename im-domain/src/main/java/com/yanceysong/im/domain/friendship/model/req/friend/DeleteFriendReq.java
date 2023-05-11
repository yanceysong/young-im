package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName DeleteFriendReq
 * @Description
 * @date 2023/5/5 10:58
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DeleteFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotBlank(message = "toId不能为空")
    private String toId;

}