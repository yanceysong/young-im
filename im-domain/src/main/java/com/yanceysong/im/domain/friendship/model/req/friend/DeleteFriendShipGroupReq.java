package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName DeleteFriendShipGroupReq
 * @Description
 * @date 2023/5/5 10:59
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class DeleteFriendShipGroupReq extends RequestBase {

    @NotBlank(message = "sendId不能为空")
    private String sendId;

    @NotEmpty(message = "分组名称不能为空")
    private List<String> groupName;

}