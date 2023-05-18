package com.yanceysong.im.domain.conversation.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.domain.conversation.model.DeleteConversationReq;
import com.yanceysong.im.domain.conversation.model.UpdateConversationReq;

/**
 * @ClassName ConversationService
 * @Description
 * @date 2023/5/17 13:41
 * @Author yanceysong
 * @Version 1.0
 */
public interface ConversationService {

    /**
     * 标记用户已读消息情况，记录 Seq 消息偏序
     * @param messageReadContent
     */
    void messageMarkRead(MessageReadContent messageReadContent);

    /**
     * 删除会话
     * @param req
     * @return
     */
    ResponseVO deleteConversation(DeleteConversationReq req);

    /**
     * 更新会话: 置顶、免打扰
     * @param req
     * @return
     */
    ResponseVO updateConversation(UpdateConversationReq req);

}
