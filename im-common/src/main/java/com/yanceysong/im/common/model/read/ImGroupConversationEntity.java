package com.yanceysong.im.common.model.read;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ImGroupConversattionEntity
 * @Description 群消息会话实体类，用一个map记录每一个消息的已读情况
 * @date 2023/6/21 14:06
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class ImGroupConversationEntity {
    /**
     * 会话id 0_sendId_receiverId
     */
    private String conversationId;

    /**
     * 会话类型
     */
    private Integer conversationType;

    private String sendId;

    /**
     * 目标对象 Id 或者群组 Id
     */
    private String receiverId;

    /**
     * 是否禁言
     */
    private int isMute;

    /**
     * 是否置顶消息
     */
    private int isTop;

    private Long sequence;

    /**
     * 消息已读偏序
     */
    private Long readSequence;

    private Integer appId;
    private Map<String, List<String>> messageReadUserMap;
}
