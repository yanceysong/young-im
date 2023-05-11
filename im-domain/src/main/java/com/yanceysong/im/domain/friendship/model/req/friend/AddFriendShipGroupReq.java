package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName AddFriendShipGroupReq
 * @Description
 * @date 2023/5/5 10:57
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddFriendShipGroupReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    public String fromId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    private List<String> toIds;

}