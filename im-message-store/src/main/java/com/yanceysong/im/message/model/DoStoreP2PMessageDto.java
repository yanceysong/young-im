package com.yanceysong.im.message.model;

import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.message.dao.ImMessageBodyEntity;
import lombok.Data;

/**
 * @ClassName DoStoreP2PMessageDto
 * @Description
 * @date 2023/5/16 11:05
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DoStoreP2PMessageDto {

    private MessageContent messageContent;

    private ImMessageBodyEntity imMessageBodyEntity;

}
