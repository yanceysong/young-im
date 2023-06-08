package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName GetUserInfoReq
 * @Description
 * @date 2023/5/5 11:23
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class GetUserInfoReq extends RequestBase {

    private List<String> userIds;

}