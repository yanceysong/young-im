package com.yanceysong.im.domain.friendship.model.callback;

import com.yanceysong.im.domain.friendship.model.req.friend.FriendDto;
import lombok.Data;

/**
 * @ClassName AddFriendAfterCallbackDto
 * @Description
 * @date 2023/5/10 9:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddFriendAfterCallbackDto {

    private String fromId;

    private FriendDto toItem;
}