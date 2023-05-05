package com.yanceysong.im.domain.friendship.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName FriendReq
 * @Description
 * @date 2023/5/5 10:53
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public abstract class FriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotNull(message = "toItem不能为空")
    private FriendDto toItem;

}