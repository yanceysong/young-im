package com.yanceysong.im.domain.friendship.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanceysong.im.codec.pack.friend.AddFriendGroupPack;
import com.yanceysong.im.codec.pack.friend.DeleteFriendGroupPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.enums.command.FriendshipEventCommand;
import com.yanceysong.im.common.enums.friend.DelFlagEnum;
import com.yanceysong.im.common.enums.friend.FriendShipErrorCode;
import com.yanceysong.im.common.model.common.ClientInfo;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupEntity;
import com.yanceysong.im.domain.friendship.dao.mapper.ImFriendShipGroupMapper;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.model.req.friend.DeleteFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupMemberService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupService;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import com.yanceysong.im.infrastructure.utils.UserSequenceRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @ClassName ImFriendShipGroupServiceImpl
 * @Description
 * @date 2023/5/5 11:06
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class ImFriendShipGroupServiceImpl implements ImFriendShipGroupService {
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private ImFriendShipGroupMapper imFriendShipGroupMapper;

    @Resource
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    @Resource
    private ImUserService imUserService;
    @Resource
    private RedisSequence redisSequence;

    @Resource
    private UserSequenceRepository userSequenceRepository;

    @Override
    @Transactional
    public ResponseVO addGroup(AddFriendShipGroupReq req) {

        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", req.getGroupName());
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getSendId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

        if (entity != null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIEND_SHIP_GROUP_SEQ);

        //写入db
        ImFriendShipGroupEntity insert = new ImFriendShipGroupEntity();
        insert.setAppId(req.getAppId());
        insert.setCreateTime(System.currentTimeMillis());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insert.setGroupName(req.getGroupName());
        insert.setSendId(req.getSendId());
        insert.setSequence(seq);
        try {
            int insert1 = imFriendShipGroupMapper.insert(insert);

            if (insert1 != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }

            if (CollectionUtil.isNotEmpty(req.getReceiverIds())) {
                AddFriendShipGroupMemberReq addFriendShipGroupMemberReq = new AddFriendShipGroupMemberReq();
                addFriendShipGroupMemberReq.setSendId(req.getSendId());
                addFriendShipGroupMemberReq.setGroupName(req.getGroupName());
                addFriendShipGroupMemberReq.setReceiverIds(req.getReceiverIds());
                addFriendShipGroupMemberReq.setAppId(req.getAppId());
                imFriendShipGroupMemberService.addGroupMember(addFriendShipGroupMemberReq);
                return ResponseVO.successResponse();
            }
        } catch (DuplicateKeyException e) {
            e.getStackTrace();
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        // 发送 TCP 通知
        AddFriendGroupPack addFriendGropPack = new AddFriendGroupPack();
        addFriendGropPack.setSendId(req.getSendId());
        addFriendGropPack.setGroupName(req.getGroupName());
        addFriendGropPack.setSequence(seq);

        messageProducer.sendToUserExceptClient(req.getSendId(), FriendshipEventCommand.FRIEND_GROUP_ADD,
                addFriendGropPack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        userSequenceRepository.writeUserSeq(req.getAppId(), req.getSendId(), SeqConstants.FRIEND_SHIP_GROUP_SEQ, seq);

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO deleteGroup(DeleteFriendShipGroupReq req) {
        for (String groupName : req.getGroupName()) {
            QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
            query.eq("group_name", groupName);
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getSendId());
            query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);
            if (entity != null) {
                long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIEND_SHIP_GROUP_SEQ);

                ImFriendShipGroupEntity update = new ImFriendShipGroupEntity();
                update.setGroupId(entity.getGroupId());
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                update.setSequence(seq);
                imFriendShipGroupMapper.updateById(update);
                imFriendShipGroupMemberService.clearGroupMember(entity.getGroupId());
                // 发送 TCP 通知
                DeleteFriendGroupPack deleteFriendGroupPack = new DeleteFriendGroupPack();
                deleteFriendGroupPack.setSendId(req.getSendId());
                deleteFriendGroupPack.setGroupName(groupName);
                deleteFriendGroupPack.setSequence(seq);
                messageProducer.sendToUserExceptClient(req.getSendId(), FriendshipEventCommand.FRIEND_GROUP_DELETE,
                        deleteFriendGroupPack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ImFriendShipGroupEntity> getGroup(String sendId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", sendId);
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }
    @Override
    public Long updateSeq(String sendId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", sendId);

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

        long seq = redisSequence.doGetSeq(appId + ":" + SeqConstants.FRIEND_SHIP_GROUP_SEQ);

        ImFriendShipGroupEntity group = new ImFriendShipGroupEntity();
        group.setGroupId(entity.getGroupId());
        group.setSequence(seq);
        imFriendShipGroupMapper.updateById(group);
        return seq;
    }


}
