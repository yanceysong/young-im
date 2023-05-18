package com.yanceysong.im.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName CheckSendMessageReq
 * @Description
 * @date 2023/5/16 9:55
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class CheckSendMessageReq {
    private String fromId;
    /**
     * [P2P] toId 为目标用户
     * [GROUP] toId 为目标群组
     */
    private String toId;

    private Integer appId;

    private Integer command;
}
