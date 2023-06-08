package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName UpdateFriendReq
 * @Description
 * @date 2023/5/5 11:02
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class UpdateFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotNull(message = "toItem不能为空")
    private FriendDto toItem;

}
