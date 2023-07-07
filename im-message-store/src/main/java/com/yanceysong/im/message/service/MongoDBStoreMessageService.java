package com.yanceysong.im.message.service;

import com.yanceysong.im.message.dao.ImGroupMessageHistoryEntity;
import com.yanceysong.im.message.dao.ImMessageHistoryEntity;
import com.yanceysong.im.message.dao.repository.MessageBodyRepository;
import com.yanceysong.im.message.dao.repository.MessageGroupHistoryRepository;
import com.yanceysong.im.message.dao.repository.MessageHistoryRepository;
import com.yanceysong.im.message.model.DoStoreGroupMessageDto;
import com.yanceysong.im.message.model.DoStoreP2PMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MongoStoreMessageService
 * @Description
 * @date 2023/7/3 10:29
 * @Author yanceysong
 * @Version 1.0
 */
@Component("mongodb")
@Slf4j
public class MongoDBStoreMessageService extends BaseMessageService implements StoreMessageService {
    @Resource
    private MessageBodyRepository messageBodyRepository;
    @Resource
    private MessageHistoryRepository messageHistoryRepository;
    @Resource
    private MessageGroupHistoryRepository messageGroupHistoryRepository;

    @Override
    public void doStoreP2PMessage(DoStoreP2PMessageDto doStoreP2PMessageDto) {
        try {
            //保存消息体
            messageBodyRepository.insertMessageBody(doStoreP2PMessageDto.getImMessageBodyEntity());
            List<ImMessageHistoryEntity> imMessageHistoryEntities = extractToP2PMessageHistory(doStoreP2PMessageDto.getMessageContent(),
                    doStoreP2PMessageDto.getImMessageBodyEntity());
            messageHistoryRepository.insertMessageHistory(imMessageHistoryEntities);
        } catch (Exception e) {
            log.error("单聊消息持久化失败 {}", e.getMessage());
        }
    }

    @Override
    public void doStoreGroupMessage(DoStoreGroupMessageDto doStoreGroupMessageDto) {
        try {
            messageBodyRepository.insertMessageBody(doStoreGroupMessageDto.getImMessageBodyEntity());
            ImGroupMessageHistoryEntity imGroupMessageHistoryEntity =
                    extractToGroupMessageHistory(doStoreGroupMessageDto.getGroupChatMessageContent(),
                            doStoreGroupMessageDto.getImMessageBodyEntity());
            messageGroupHistoryRepository.insertMessageGroupHistory(imGroupMessageHistoryEntity);
        } catch (Exception e) {
            log.error("群聊消息持久化失败 {}", e.getMessage());
        }
    }
}
