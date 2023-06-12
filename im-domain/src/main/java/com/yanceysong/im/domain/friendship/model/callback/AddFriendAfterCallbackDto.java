package com.yanceysong.im.domain.friendship.model.callback;

import com.yanceysong.im.domain.friendship.model.req.friend.FriendDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName AddFriendAfterCallbackDto
 * @Description
 * @date 2023/5/10 9:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class AddFriendAfterCallbackDto {

    private String sendId;

    private FriendDto toItem;
}