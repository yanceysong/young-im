package com.yanceysong.im.message.dao.repository;

import com.yanceysong.im.message.dao.ImMessageBodyEntity;
import com.yanceysong.im.message.dao.ImMessageHistoryEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName MongoDBRepository
 * @Description
 * @date 2023/7/3 11:25
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class MessageBodyRepository {
    @Resource
    private MongoTemplate mongoTemplate;

    public void insertMessageBody(ImMessageBodyEntity messageBody) {
        mongoTemplate.insert(messageBody);
    }
    public void insertMessageHistory (List<ImMessageHistoryEntity> imMessageHistoryEntityList){
        mongoTemplate.insert(imMessageHistoryEntityList,ImMessageHistoryEntity.class);
    }

}
