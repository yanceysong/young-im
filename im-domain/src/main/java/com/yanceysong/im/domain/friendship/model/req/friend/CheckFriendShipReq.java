package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @ClassName CheckFriendShipReq
 * @Description
 * @date 2023/5/5 10:57
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class CheckFriendShipReq extends RequestBase {

    @NotBlank(message = "sendId不能为空")
    private String sendId;

    @NotEmpty(message = "receiverIds不能为空")
    private List<String> receiverIds;

    @NotNull(message = "checkType不能为空")
    private Integer checkType;
}