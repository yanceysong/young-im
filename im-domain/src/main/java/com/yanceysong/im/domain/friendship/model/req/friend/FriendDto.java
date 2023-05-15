package com.yanceysong.im.domain.friendship.model.req.friend;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName FriendDto
 * @Description
 * @date 2023/5/5 11:00
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class FriendDto {

    private String toId;

    private String remark;

    private String addSource;

    private String extra;

    private String addWording;

}
