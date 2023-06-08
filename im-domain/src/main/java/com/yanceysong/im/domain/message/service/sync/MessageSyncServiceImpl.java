package com.yanceysong.im.domain.message.service.sync;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yanceysong.im.codec.pack.message.MessageReadPack;
import com.yanceysong.im.codec.pack.message.RecallMessageNotifyPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.enums.conversation.ConversationTypeEnum;
import com.yanceysong.im.common.enums.error.MessageErrorCode;
import com.yanceysong.im.common.enums.friend.DelFlagEnum;
import com.yanceysong.im.common.exception.YoungImErrorMsg;
import com.yanceysong.im.common.model.RecallMessageContent;
import com.yanceysong.im.common.model.common.ClientInfo;
import com.yanceysong.im.common.model.content.MessageReceiveAckContent;
import com.yanceysong.im.common.model.content.OfflineMessageContent;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.common.model.sync.SyncReq;
import com.yanceysong.im.common.model.sync.SyncResp;
import com.yanceysong.im.domain.conversation.service.ConversationService;
import com.yanceysong.im.domain.group.GroupMessageProducer;
import com.yanceysong.im.domain.group.service.ImGroupMemberService;
import com.yanceysong.im.domain.message.dao.ImMessageBodyEntity;
import com.yanceysong.im.domain.message.dao.mapper.ImMessageBodyMapper;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import com.yanceysong.im.infrastructure.supports.ids.ConversationIdGenerate;
import com.yanceysong.im.infrastructure.supports.ids.SnowflakeIdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @ClassName MessageSyncServiceImpl
 * @Description 消息同步服务类 用于处理消息接收确认，同步等操作
 * @date 2023/5/17 13:50
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class MessageSyncServiceImpl implements MessageSyncService {

    @Resource
    private MessageProducer messageProducer;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private ImMessageBodyMapper imMessageBodyMapper;
    @Resource
    private ConversationService conversationService;
    @Resource
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private RedisSequence redisSequence;
    @Resource
    private GroupMessageProducer groupMessageProducer;

    @Override
    public void receiveMark(MessageReceiveAckContent pack) {
        // 确认接收 ACK 发送给在线目标用户全端
        messageProducer.sendToUserAllClient(pack.getToId(),
                MessageCommand.MSG_RECEIVE_ACK, pack, pack.getAppId());
    }

    /**
     * 增量拉取消息 从redis拉取并且回复给客户端
     *
     * @param req 请求
     * @return 增量消息
     */
    @Override
    public ResponseVO<SyncResp<OfflineMessageContent>> syncOfflineMessage(SyncReq req) {
        SyncResp<OfflineMessageContent> resp = new SyncResp<>();
        String key = req.getAppId() + ":" + RedisConstants.OFFLINE_MESSAGE + ":" + req.getOperator();
        //获取最大的seq
        long maxSeq = 0L;
        //返回zset的最大分数值。
        Set<ZSetOperations.TypedTuple<String>> set = redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, 0);
        if (!CollectionUtils.isEmpty(set)) {
            List<ZSetOperations.TypedTuple<String>> list = new ArrayList<>(set);
            ZSetOperations.TypedTuple<String> o = list.get(0);
            maxSeq = Objects.requireNonNull(o.getScore()).longValue();
        }
        List<OfflineMessageContent> respList = new ArrayList<>();
        resp.setMaxSequence(maxSeq);
        //根据客户端传来的seq和查到的最大seq拉取区间内一定数量的消息
        Set<ZSetOperations.TypedTuple<String>> querySet = redisTemplate.opsForZSet().rangeByScoreWithScores(key,
                req.getLastSequence(),
                maxSeq,
                0,
                req.getMaxLimit());
        if (querySet == null || querySet.size() == 0) {
            return ResponseVO.successResponse(resp);
        }
        for (ZSetOperations.TypedTuple<String> typedTuple : querySet) {
            String value = typedTuple.getValue();
            OfflineMessageContent offlineMessageContent = JSONObject.parseObject(value, OfflineMessageContent.class);
            respList.add(offlineMessageContent);
        }
        resp.setDataList(respList);
        if (!CollectionUtils.isEmpty(respList)) {
            OfflineMessageContent offlineMessageContent = respList.get(respList.size() - 1);
            //判断是否拉完了。
            resp.setCompleted(maxSeq <= offlineMessageContent.getMessageKey());
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    public void readMark(MessageReadContent messageContent, Command notify, Command receipt) {
        //更新会话的seq
        conversationService.messageMarkRead(messageContent);
        MessageReadPack messageReadPack = Content2Pack(messageContent);
        //同步给自己的所有端
        syncToSender(messageReadPack, messageContent, notify);
        // 防止自己给自己发送消息
        if (!messageContent.getFromId().equals(messageContent.getToId())) {
            // 发送给对方
            messageProducer.sendToUserAllClient(
                    messageContent.getToId(),
                    receipt,
                    messageReadPack,
                    messageContent.getAppId()
            );
        }
    }

    /**
     * 同步给自己的所有端
     *
     * @param pack    MessageReadPack要发送的包
     * @param content message已读上下文
     * @param command 指令 MSG_READ_NOTIFY(0x41D) 表名这个消息自己已经读了
     */
    private void syncToSender(MessageReadPack pack, MessageReadContent content, Command command) {
        messageProducer.sendToUserExceptClient(content.getFromId(), command, pack, content);
    }

    private MessageReadPack Content2Pack(MessageReadContent messageContent) {
        MessageReadPack messageReadPack = new MessageReadPack();
        messageReadPack.setMessageSequence(messageContent.getMessageSequence());
        messageReadPack.setFromId(messageContent.getFromId());
        messageReadPack.setToId(messageContent.getToId());
        messageReadPack.setGroupId(messageContent.getGroupId());
        messageReadPack.setConversationType(messageContent.getConversationType());
        return messageReadPack;
    }

    /**
     * 撤回消息处理
     *
     * @param content 请求上下文
     */
    @Override
    public void recallMessage(RecallMessageContent content) {
        RecallMessageNotifyPack pack = new RecallMessageNotifyPack();
        BeanUtils.copyProperties(content, pack);
        //修改历史消息的状态
        ImMessageBodyEntity body = updateMessageHistoryState(content, pack);
        if (body == null) {
            //消息不存在
            recallAck(pack, ResponseVO.errorResponse(YoungImErrorMsg.UNKNOWN_ERROR_FOR_RECALL), content);
            return;
        }
        //  如果是p2p的消息
        if (Objects.equals(content.getConversationType(), ConversationTypeEnum.P2P.getCode())) {
            updateP2POfflineMessageState(content, pack, body);
            return;
        }
        updateGroupOfflineMessageState(content, pack, body);
    }

    /**
     * 更新群组离线消息状态
     *
     * @param content 撤回消息请求上下文
     * @param pack    撤回消息发送包
     * @param body    消息体
     */
    private void updateGroupOfflineMessageState(RecallMessageContent content, RecallMessageNotifyPack pack, ImMessageBodyEntity body) {
        List<String> groupMemberId = imGroupMemberService.getGroupMemberId(content.getToId(), content.getAppId());
        long seq = redisSequence.doGetSeq(content.getAppId() + ":" + SeqConstants.MESSAGE_SEQ + ":" + ConversationIdGenerate.generateP2PId(content.getFromId(), content.getToId()));
        //ack发送端标识服务端已经处理了
        recallAck(pack, ResponseVO.successResponse(), content);
        //发送给同步端
        messageProducer.sendToUserExceptClient(content.getFromId(),
                MessageCommand.MSG_RECALL_NOTIFY,
                pack,
                content);
        for (String memberId : groupMemberId) {
            String toKey = content.getAppId() + ":" + SeqConstants.MESSAGE_SEQ + ":" + memberId;
            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
            BeanUtils.copyProperties(content, offlineMessageContent);
            offlineMessageContent.setConversationType(ConversationTypeEnum.GROUP.getCode());
            offlineMessageContent.setConversationId(conversationService.convertConversationId(offlineMessageContent.getConversationType()
                    , content.getFromId(), content.getToId()));
            offlineMessageContent.setMessageBody(body.getMessageBody());
            offlineMessageContent.setMessageSequence(seq);
            redisTemplate.opsForZSet().add(toKey, JSONObject.toJSONString(offlineMessageContent), seq);
            groupMessageProducer.producer(content.getFromId(), MessageCommand.MSG_RECALL_NOTIFY, pack, content);
        }
    }

    /**
     * 更新个人离线消息状态
     *
     * @param content 撤回消息请求上下文
     * @param pack    撤回消息发送包
     * @param body    消息体
     */
    private void updateP2POfflineMessageState(RecallMessageContent content, RecallMessageNotifyPack pack, ImMessageBodyEntity body) {
        // 找到fromId的队列
        String fromKey = content.getAppId() + ":" + RedisConstants.OFFLINE_MESSAGE + ":" + content.getFromId();
        // 找到toId的队列
        String toKey = content.getAppId() + ":" + RedisConstants.OFFLINE_MESSAGE + ":" + content.getToId();
        //组装离线消息
        OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
        BeanUtils.copyProperties(content, offlineMessageContent);
        offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
        offlineMessageContent.setMessageKey(content.getMessageKey());
        offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
        offlineMessageContent.setConversationId(conversationService.convertConversationId(offlineMessageContent.getConversationType()
                , content.getFromId(), content.getToId()));
        offlineMessageContent.setMessageBody(body.getMessageBody());
        //
        long seq = redisSequence.doGetSeq(content.getAppId() + ":" + SeqConstants.MESSAGE_SEQ + ":" + ConversationIdGenerate.generateP2PId(content.getFromId(), content.getToId()));
        offlineMessageContent.setMessageSequence(seq);

        long messageKey = SnowflakeIdWorker.nextId();

        redisTemplate.opsForZSet().add(fromKey, JSONObject.toJSONString(offlineMessageContent), messageKey);
        redisTemplate.opsForZSet().add(toKey, JSONObject.toJSONString(offlineMessageContent), messageKey);

        //ack
        recallAck(pack, ResponseVO.successResponse(), content);
        //分发给同步端
        messageProducer.sendToUserExceptClient(content.getFromId(),
                MessageCommand.MSG_RECALL_NOTIFY, pack, content);
        //分发给对方
        messageProducer.sendToUserAllClient(content.getToId(), MessageCommand.MSG_RECALL_NOTIFY,
                pack, content.getAppId());
    }

    /**
     * 修改历史状态，如果无需修改则返回null
     *
     * @param content 撤回消息请求上下文
     * @param pack    撤回消息包
     * @return ImMessageBodyEntity 消息体
     */
    private ImMessageBodyEntity updateMessageHistoryState(RecallMessageContent content, RecallMessageNotifyPack pack) {
        Long messageTime = content.getMessageTime();
        Long now = System.currentTimeMillis();
        // 超过两分钟的消息不能够撤回
        if (120000L < now - messageTime) {
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_RECALL_TIME_OUT), content);
            return null;
        }
        //查询到messageBody
        QueryWrapper<ImMessageBodyEntity> query = new QueryWrapper<>();
        query.eq("app_id", content.getAppId());
        query.eq("message_key", content.getMessageKey());
        ImMessageBodyEntity body = imMessageBodyMapper.selectOne(query);
        if (body == null) {
            // ack失败 不存在的消息不能撤回
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_BODY_IS_NOT_EXIST), content);
            return null;
        }
        //消息已经被撤回不能重复撤回
        if (body.getDelFlag() == DelFlagEnum.DELETE.getCode()) {
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_IS_RECALLED), content);
            return null;
        }
        //设置撤回标志
        body.setDelFlag(DelFlagEnum.DELETE.getCode());
        //更新
        return imMessageBodyMapper.update(body, query) > 0 ? body : null;
    }

    private void recallAck(RecallMessageNotifyPack recallPack, ResponseVO<ResponseVO.NoDataReturn> success, ClientInfo clientInfo) {
        messageProducer.sendToUserOneClient(recallPack.getFromId(),
                MessageCommand.MSG_RECALL_ACK, success, clientInfo);
    }

}
