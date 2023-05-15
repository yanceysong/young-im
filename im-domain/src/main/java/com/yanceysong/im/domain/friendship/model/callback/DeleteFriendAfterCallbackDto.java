package com.yanceysong.im.domain.friendship.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName DeleteFriendAfterCallbackDto
 * @Description
 * @date 2023/5/10 9:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class DeleteFriendAfterCallbackDto {

    private String fromId;

    private String toId;
}
