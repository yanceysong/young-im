package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName AddFriendShipGroupMemberReq
 * @Description
 * @date 2023/5/5 10:56
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class AddFriendShipGroupMemberReq extends RequestBase {

    @NotBlank(message = "sendId不能为空")
    private String sendId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotEmpty(message = "请选择用户")
    private List<String> receiverIds;


}

