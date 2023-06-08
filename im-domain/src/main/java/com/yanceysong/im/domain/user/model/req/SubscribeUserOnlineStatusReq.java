package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName SubscribeUserOnlineStatusReq
 * @Description
 * @date 2023/6/8 11:57
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class SubscribeUserOnlineStatusReq extends RequestBase {

    private List<String> subUserId;

    private Long subTime;


}