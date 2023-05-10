package com.yanceysong.im.domain.friendship.model.callback;

import lombok.Data;

/**
 * @ClassName AddFriendBlackAfterCallbackDto
 * @Description
 * @date 2023/5/10 9:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddFriendBlackAfterCallbackDto {

    private String fromId;

    private String toId;
}