package com.yanceysong.im.common.model.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName RequestBase
 * @Description
 * @date 2023/4/28 11:00
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class RequestBase {
    /**
     * APP ID
     */
    private Integer appId;

    /**
     * 操作人，谁在调用接口
     */
    private String operator;

    private Integer clientType;

    private String imei;
}
