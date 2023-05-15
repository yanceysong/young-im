package com.yanceysong.im.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @ClassName RequestBase
 * @Description
 * @date 2023/4/28 11:00
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
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
