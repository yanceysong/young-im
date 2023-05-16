package com.yanceysong.im.message.model;

import com.yanceysong.im.common.model.GroupChatMessageContent;
import com.yanceysong.im.message.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * @ClassName DoStoreGroupMessageDto
 * @Description
 * @date 2023/5/16 11:05
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DoStoreGroupMessageDto {

    private GroupChatMessageContent groupChatMessageContent;

    private ImMessageBodyEntity imMessageBodyEntity;

}