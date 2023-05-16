package com.yanceysong.im.message.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName ImMessageBodyEntity
 * @Description
 * @date 2023/5/16 11:05
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@TableName("im_message_body")
public class ImMessageBodyEntity {

    private Integer appId;

    /** messageBody 消息实体唯一 ID 标识 */
    private Long messageKey;

    private String messageBody;

    /** 消息加密密钥，防止消息被黑客截取 */
    private String securityKey;

    private Long messageTime;

    private Long createTime;

    private String extra;

    private Integer delFlag;

}