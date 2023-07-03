package com.yanceysong.im.message.dao.repository;

import com.yanceysong.im.message.dao.ImGroupMessageHistoryEntity;
import com.yanceysong.im.message.dao.ImMessageHistoryEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MessageGroupHistoryRepository
 * @Description
 * @date 2023/7/3 11:57
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class MessageGroupHistoryRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public void insertMessageGroupHistory(ImGroupMessageHistoryEntity imGroupMessageHistoryEntities) {
        mongoTemplate.insert(imGroupMessageHistoryEntities);
    }
}
