package com.yanceysong.im.domain.friendship.model.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName AddFriendBlackAfterCallbackDto
 * @Description
 * @date 2023/5/10 9:46
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class AddFriendBlackAfterCallbackDto {

    private String sendId;

    private String receiverId;
}