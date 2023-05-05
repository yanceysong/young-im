package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

/**
 * @ClassName GetUserSequenceReq
 * @Description
 * @date 2023/5/5 11:23
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;

}

