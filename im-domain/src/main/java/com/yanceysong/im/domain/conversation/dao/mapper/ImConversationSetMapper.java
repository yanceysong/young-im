package com.yanceysong.im.domain.conversation.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanceysong.im.domain.conversation.dao.ImConversationSetEntity;
import org.springframework.stereotype.Repository;

/**
 * @ClassName ImConversationSetMapper
 * @Description
 * @date 2023/5/17 13:39
 * @Author yanceysong
 * @Version 1.0
 */
@Repository
public interface ImConversationSetMapper extends BaseMapper<ImConversationSetEntity> {

    void readMark(ImConversationSetEntity imConversationSetEntity);

    Long getConversationSetMaxSeq(Integer appId, String userId);
    /**
     * 增量拉取会话消息一次最大条目数
     * @param appId
     * @return
     */
    Long getConversationMaxSeq(Integer appId);
}
