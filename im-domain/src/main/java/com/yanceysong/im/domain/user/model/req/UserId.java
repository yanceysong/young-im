package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

/**
 * @ClassName UserId
 * @Description
 * @date 2023/5/5 11:24
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UserId extends RequestBase {

    private String userId;

}