package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName UserId
 * @Description
 * @date 2023/5/5 11:24
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class UserId extends RequestBase {

    private String userId;

}