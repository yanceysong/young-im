package com.yanceysong.im.common.model;

import com.yanceysong.im.common.enums.message.MessageContent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName GroupChatMessageContent
 * @Description
 * @date 2023/5/16 9:56
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class GroupChatMessageContent extends MessageContent {
    private String groupId;

    private List<String> memberId;
}
