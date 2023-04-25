package com.yanceysong.im.common.model;

import lombok.Data;

/**
 * @ClassName UserClientDto
 * @Description
 * @date 2023/4/25 11:18
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UserClientDto {
    private String userId;

    private Integer appId;

    private Integer clientType;
}
