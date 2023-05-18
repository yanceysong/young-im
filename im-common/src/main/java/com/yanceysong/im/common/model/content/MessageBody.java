package com.yanceysong.im.common.model.content;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName MessageBody
 * @Description
 * @date 2023/5/16 9:56
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class MessageBody {
    private Integer appId;

    /** messageBodyId*/
    private Long messageKey;

    /** messageBody*/
    private String messageBody;

    private String securityKey;

    private Long messageTime;

    private Long createTime;

    private String extra;

    private Integer delFlag;
}
