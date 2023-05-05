package com.yanceysong.im.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ClientInfo
 * @Description
 * @date 2023/4/28 11:00
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class ClientInfo {
    private Integer appId;

    private Integer clientType;

    private String imei;

    public ClientInfo(Integer appId, Integer clientType, String imei) {
        this.appId = appId;
        this.clientType = clientType;
        this.imei = imei;
    }
}
