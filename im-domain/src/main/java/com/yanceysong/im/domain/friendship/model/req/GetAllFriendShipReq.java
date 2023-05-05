package com.yanceysong.im.domain.friendship.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName GetAllFriendShipReq
 * @Description 获取全部好友
 * @date 2023/5/5 11:00
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class GetAllFriendShipReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;
}
