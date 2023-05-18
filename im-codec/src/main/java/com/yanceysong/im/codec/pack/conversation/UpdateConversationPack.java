package com.yanceysong.im.codec.pack.conversation;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName UpdateConversationPack
 * @Description
 * @date 2023/5/17 13:26
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@ToString(doNotUseGetters = true)
public class UpdateConversationPack {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private Integer conversationType;

    private Long sequence;

}
