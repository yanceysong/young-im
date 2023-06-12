package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName AddFriendShipBlackReq
 * @Description
 * @date 2023/5/5 10:56
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class AddFriendShipBlackReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String sendId;

    private String receiverId;
}

