package com.yanceysong.im.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName SyncReq
 * @Description
 * @date 2023/5/18 14:08
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(doNotUseGetters = true)
public class SyncReq extends RequestBase {

    /** 客户端最大 Seq */
    private Long lastSequence;

    /** 一次性最大拉取次数 */
    private Integer maxLimit;

}
