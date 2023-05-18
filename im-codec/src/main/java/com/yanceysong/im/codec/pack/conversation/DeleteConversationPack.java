package com.yanceysong.im.codec.pack.conversation;

import lombok.Data;
import lombok.ToString;

/**
 * @ClassName DeleteConversationPack
 * @Description
 * @date 2023/5/17 13:26
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@ToString(doNotUseGetters = true)
public class DeleteConversationPack {

    private String conversationId;

}
