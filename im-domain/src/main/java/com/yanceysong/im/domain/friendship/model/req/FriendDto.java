package com.yanceysong.im.domain.friendship.model.req;

import lombok.Data;

/**
 * @ClassName FriendDto
 * @Description
 * @date 2023/5/5 11:00
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class FriendDto {

    private String toId;

    private String remark;

    private String addSource;

    private String extra;

    private String addWording;

}