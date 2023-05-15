package com.yanceysong.im.domain.user.model.req;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * @ClassName LoginReq
 * @Description
 * @date 2023/5/6 10:52
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=false)
@ToString(doNotUseGetters=true)
public class LoginReq {

    @NotNull(message = "用户 ID 不能为空")
    private String userId;

    @NotNull(message = "appId 不能为空")
    private Integer appId;

    private Integer clientType;
}
