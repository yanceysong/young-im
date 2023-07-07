package com.yanceysong.im.message.service;

import com.yanceysong.im.common.model.content.MessageContent;
import com.yanceysong.im.message.dao.ImMessageBodyEntity;
import com.yanceysong.im.message.dao.ImMessageHistoryEntity;
import com.yanceysong.im.message.model.DoStoreGroupMessageDto;
import com.yanceysong.im.message.model.DoStoreP2PMessageDto;

import java.util.List;

/**
 * @ClassName StoreMessageService
 * @Description
 * @date 2023/7/3 10:27
 * @Author yanceysong
 * @Version 1.0
 */
public interface StoreMessageService {
    void doStoreP2PMessage(DoStoreP2PMessageDto doStoreP2PMessageDto);
    void doStoreGroupMessage(DoStoreGroupMessageDto doStoreGroupMessageDto);

//    List<ImMessageHistoryEntity> extractToP2PMessageHistory(
//            MessageContent messageContent, ImMessageBodyEntity imMessageBodyEntity);
}
