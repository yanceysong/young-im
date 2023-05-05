package com.yanceysong.im.domain.group.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @ClassName ImGroupMessageHistoryEntity
 * @Description
 * @date 2023/5/5 11:47
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@TableName("im_group_message_history")
public class ImGroupMessageHistoryEntity {

    private Integer appId;

    private String fromId;

    private String groupId;

    /** messageBodyId*/
    private Long messageKey;
    /** 序列号*/
    private Long sequence;

    private String messageRandom;

    private Long messageTime;

    private Long createTime;


}