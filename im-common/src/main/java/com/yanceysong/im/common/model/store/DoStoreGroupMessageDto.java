package com.yanceysong.im.common.model.store;

import com.yanceysong.im.common.model.GroupChatMessageContent;
import com.yanceysong.im.common.enums.message.MessageBody;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName DoStoreGroupMessageDto
 * @Description
 * @date 2023/5/16 9:55
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class DoStoreGroupMessageDto {
    private GroupChatMessageContent groupChatMessageContent;

    private MessageBody messageBody;

}
