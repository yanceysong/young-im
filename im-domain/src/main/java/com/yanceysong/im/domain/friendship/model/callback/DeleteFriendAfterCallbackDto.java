package com.yanceysong.im.domain.friendship.model.callback;

import lombok.Data;

/**
 * @ClassName DeleteFriendAfterCallbackDto
 * @Description
 * @date 2023/5/10 9:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DeleteFriendAfterCallbackDto {

    private String fromId;

    private String toId;
}
