package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName PullUserOnlineStatusReq
 * @Description
 * @date 2023/6/8 11:56
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class PullUserOnlineStatusReq extends RequestBase {

    private List<String> userList;

}
