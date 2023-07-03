package com.yanceysong.im.message.dao.repository;

import com.yanceysong.im.message.dao.ImGroupMessageHistoryEntity;
import com.yanceysong.im.message.dao.ImMessageHistoryEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MessageHistoryRepository
 * @Description
 * @date 2023/7/3 11:56
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class MessageHistoryRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public void insertMessageHistory(List<ImMessageHistoryEntity> imGroupMessageHistoryEntities) {
        mongoTemplate.insert(imGroupMessageHistoryEntities, ImMessageHistoryEntity.class);
    }
}
