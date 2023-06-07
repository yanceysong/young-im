package com.yanceysong.im.domain.message.service.sync;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yanceysong.im.codec.pack.message.MessageReadPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.common.model.SyncReq;
import com.yanceysong.im.common.model.SyncResp;
import com.yanceysong.im.common.model.content.MessageReceiveAckContent;
import com.yanceysong.im.common.model.content.OfflineMessageContent;
import com.yanceysong.im.common.model.read.MessageReadContent;
import com.yanceysong.im.domain.conversation.service.ConversationService;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import org.springframework.data.redis.core.DefaultTypedTuple;
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
    private RedisTemplate<String,String> redisTemplate;
    @Resource
    private ConversationService conversationServiceImpl;

    @Override
    public void receiveMark(MessageReceiveAckContent pack) {
        // 确认接收 ACK 发送给在线目标用户全端
        messageProducer.sendToUserAllClient(pack.getToId(),
                MessageCommand.MSG_RECEIVE_ACK, pack, pack.getAppId());
    }
    @Override
    public ResponseVO<SyncResp<OfflineMessageContent>> syncOfflineMessage(SyncReq req) {

        SyncResp<OfflineMessageContent> resp = new SyncResp<>();

        String key = req.getAppId() + ":" + RedisConstants.OFFLINE_MESSAGE + ":" + req.getOperator();
        //获取最大的seq
        Long maxSeq = 0L;
        ZSetOperations zSetOperations = redisTemplate.opsForZSet();
        Set set = zSetOperations.reverseRangeWithScores(key, 0, 0);
        if(!CollectionUtils.isEmpty(set)){
            List list = new ArrayList(set);
            DefaultTypedTuple o = (DefaultTypedTuple) list.get(0);
            maxSeq = Objects.requireNonNull(o.getScore()).longValue();
        }

        List<OfflineMessageContent> respList = new ArrayList<>();
        resp.setMaxSequence(maxSeq);

        Set<ZSetOperations.TypedTuple> querySet = zSetOperations.rangeByScoreWithScores(
                key, req.getLastSequence(), maxSeq, 0, req.getMaxLimit());
        for (ZSetOperations.TypedTuple<String> typedTuple : querySet) {
            String value = typedTuple.getValue();
            OfflineMessageContent offlineMessageContent = JSONObject.parseObject(value, OfflineMessageContent.class);
            respList.add(offlineMessageContent);
        }
        resp.setDataList(respList);

        if(!CollectionUtils.isEmpty(respList)){
            OfflineMessageContent offlineMessageContent = respList.get(respList.size() - 1);
            resp.setCompleted(maxSeq <= offlineMessageContent.getMessageKey());
        }

        return ResponseVO.successResponse(resp);
    }

    @Override
    public void readMark(MessageReadContent messageContent, Command notify, Command receipt) {
        //更新会话的seq
        conversationServiceImpl.messageMarkRead(messageContent);
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

}
