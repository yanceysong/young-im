package com.yanceysong.im.domain.conversation.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.common.model.sync.SyncReq;
import com.yanceysong.im.common.model.sync.SyncResp;
import com.yanceysong.im.domain.conversation.dao.ImConversationSetEntity;
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
     *
     * @param messageReadContent 已读消息上下文
     */
    void messageMarkRead(MessageReadContent messageReadContent);

    /**
     * 删除会话
     *
     * @param req 请求
     * @return 是否成功
     */
    ResponseVO<ResponseVO.NoDataReturn> deleteConversation(DeleteConversationReq req);

    /**
     * 更新会话: 置顶、免打扰
     *
     * @param req 请求
     * @return 是否成功
     */
    ResponseVO<ResponseVO.NoDataReturn> updateConversation(UpdateConversationReq req);

    ResponseVO<SyncResp<ImConversationSetEntity>> syncConversationSet(SyncReq req);

    /**
     * 生成会话id
     *
     * @param type   会话类型
     * @param fromId from
     * @param toId   to
     * @return 会话id
     */
    String convertConversationId(Integer type, String fromId, String toId);
}
