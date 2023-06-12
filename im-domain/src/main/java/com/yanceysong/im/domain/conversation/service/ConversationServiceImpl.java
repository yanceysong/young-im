package com.yanceysong.im.domain.conversation.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yanceysong.im.codec.pack.conversation.DeleteConversationPack;
import com.yanceysong.im.codec.pack.conversation.UpdateConversationPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.enums.command.ConversationEventCommand;
import com.yanceysong.im.common.enums.conversation.ConversationTypeEnum;
import com.yanceysong.im.common.enums.error.ConversationErrorCode;
import com.yanceysong.im.common.model.common.ClientInfo;
import com.yanceysong.im.common.model.sync.SyncReq;
import com.yanceysong.im.common.model.sync.SyncResp;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.domain.conversation.dao.ImConversationSetEntity;
import com.yanceysong.im.domain.conversation.dao.mapper.ImConversationSetMapper;
import com.yanceysong.im.domain.conversation.model.DeleteConversationReq;
import com.yanceysong.im.domain.conversation.model.UpdateConversationReq;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.infrastructure.config.AppConfig;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import com.yanceysong.im.infrastructure.utils.UserSequenceRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ConversationServiceImpl
 * @Description
 * @date 2023/5/17 13:42
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class ConversationServiceImpl implements ConversationService {

    @Resource
    private AppConfig appConfig;
    @Resource
    private ImConversationSetMapper imConversationSetMapper;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private RedisSequence redisSequence;
    @Resource
    private UserSequenceRepository userSequenceRepository;

    public String convertConversationId(Integer type, String sendId, String receiverId) {
        return type + "_" + sendId + "_" + receiverId;
    }

    @Override
    public void messageMarkRead(MessageReadContent messageReadContent) {
        // 抽离 receiverId, 有不同情况
        // 会话类型为单聊，receiverId 赋值为目标用户
        String receiverId = messageReadContent.getReceiverId();
        if (ConversationTypeEnum.GROUP.getCode().equals(messageReadContent.getConversationType())) {
            // 会话类型为群聊，receiverId 赋值为 groupId
            receiverId = messageReadContent.getGroupId();
        }
        // conversationId: 1_sendId_receiverId
        String conversationId = convertConversationId(
                messageReadContent.getConversationType(), messageReadContent.getSendId(), receiverId);
        QueryWrapper<ImConversationSetEntity> query = new QueryWrapper<>();
        query.eq("app_id", messageReadContent.getAppId());
        query.eq("conversation_id", conversationId);
        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(query);
        if (imConversationSetEntity == null) {
            // 如果查询记录为空，代表不存在该会话，需要新建
            imConversationSetEntity = new ImConversationSetEntity();
            long seq = redisSequence.doGetSeq(messageReadContent.getAppId() + ":" + SeqConstants.CONVERSATION_SEQ);
            imConversationSetEntity.setConversationId(conversationId);
            imConversationSetEntity.setSequence(seq);
            imConversationSetEntity.setConversationType(messageReadContent.getConversationType());
            imConversationSetEntity.setSendId(messageReadContent.getSendId());
            imConversationSetEntity.setReceiverId(receiverId);
            imConversationSetEntity.setAppId(messageReadContent.getAppId());
            imConversationSetEntity.setReadSequence(messageReadContent.getMessageSequence());

            imConversationSetMapper.insert(imConversationSetEntity);
            userSequenceRepository.writeUserSeq(messageReadContent.getAppId(),
                    messageReadContent.getSendId(), SeqConstants.CONVERSATION_SEQ, seq);
        } else {
            //本地会话的更新也是根据一个seq进行的。所以每次已读一个消息，这个会话的seq就最高
            long seq = redisSequence.doGetSeq(
                    messageReadContent.getAppId() + ":" + SeqConstants.CONVERSATION_SEQ);
            imConversationSetEntity.setSequence(seq);
            imConversationSetEntity.setReadSequence(messageReadContent.getMessageSequence());
            //更新数据库
            imConversationSetMapper.readMark(imConversationSetEntity);
            //记录这个用户的会话最大seq
            userSequenceRepository.writeUserSeq(messageReadContent.getAppId(),
                    messageReadContent.getSendId(), SeqConstants.CONVERSATION_SEQ, seq);
        }
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> deleteConversation(DeleteConversationReq req) {
        if (appConfig.getDeleteConversationSyncMode() == 1) {
            DeleteConversationPack pack = new DeleteConversationPack();
            pack.setConversationId(req.getConversationId());
            messageProducer.sendToUserExceptClient(req.getSendId(),
                    ConversationEventCommand.CONVERSATION_DELETE,
                    pack, new ClientInfo(req.getAppId(), req.getClientType(),
                            req.getImei()));
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> updateConversation(UpdateConversationReq req) {
        if (req.getIsTop() == null && req.getIsMute() == null) {
            return ResponseVO.errorResponse(ConversationErrorCode.CONVERSATION_UPDATE_PARAM_ERROR);
        }
        QueryWrapper<ImConversationSetEntity> query = new QueryWrapper<>();
        query.eq("conversation_id", req.getConversationId());
        query.eq("app_id", req.getAppId());
        ImConversationSetEntity imConversationSetEntity = imConversationSetMapper.selectOne(query);
        if (imConversationSetEntity != null) {
            long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.CONVERSATION_SEQ);
            if (req.getIsMute() != null) {
                // 更新禁言状态
                imConversationSetEntity.setIsMute(req.getIsMute());
            }
            if (req.getIsTop() != null) {
                // 更新置顶状态
                imConversationSetEntity.setIsTop(req.getIsTop());
            }
            imConversationSetEntity.setSequence(seq);
            imConversationSetMapper.update(imConversationSetEntity, query);
            userSequenceRepository.writeUserSeq(req.getAppId(),
                    req.getSendId(), SeqConstants.CONVERSATION_SEQ, seq);

            UpdateConversationPack pack = new UpdateConversationPack();
            pack.setConversationId(req.getConversationId());
            pack.setIsMute(imConversationSetEntity.getIsMute());
            pack.setIsTop(imConversationSetEntity.getIsTop());
            pack.setSequence(seq);
            pack.setConversationType(imConversationSetEntity.getConversationType());
            messageProducer.sendToUserExceptClient(req.getSendId(),
                    ConversationEventCommand.CONVERSATION_UPDATE,
                    pack, new ClientInfo(req.getAppId(), req.getClientType(),
                            req.getImei()));
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<SyncResp<ImConversationSetEntity>> syncConversationSet(SyncReq req) {
        if (req.getMaxLimit() > appConfig.getConversationMaxCount()) {
            req.setMaxLimit(appConfig.getConversationMaxCount());
        }

        SyncResp<ImConversationSetEntity> resp = new SyncResp<>();

        QueryWrapper<ImConversationSetEntity> query = new QueryWrapper<>();
        query.eq("send_id", req.getOperator());
        query.gt("sequence", req.getLastSequence());
        query.eq("app_id", req.getAppId());
        query.last("limit " + req.getMaxLimit());
        query.orderByAsc("sequence");
        List<ImConversationSetEntity> list = imConversationSetMapper.selectList(query);

        if (!CollectionUtils.isEmpty(list)) {
            ImConversationSetEntity maxSeqEntity = list.get(list.size() - 1);
            resp.setDataList(list);
            // 设置最大 Seq
            Long conversationMaxSeq = imConversationSetMapper.getConversationMaxSeq(req.getAppId());
            resp.setMaxSequence(conversationMaxSeq);
            // 设置是否拉取完毕
            resp.setCompleted(maxSeqEntity.getSequence() >= conversationMaxSeq);
            return ResponseVO.successResponse(resp);
        }
        resp.setCompleted(true);
        return ResponseVO.successResponse(resp);
    }

}

