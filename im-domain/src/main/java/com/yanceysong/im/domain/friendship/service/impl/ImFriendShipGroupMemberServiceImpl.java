package com.yanceysong.im.domain.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanceysong.im.codec.pack.friend.AddFriendGroupMemberPack;
import com.yanceysong.im.codec.pack.friend.DeleteFriendGroupMemberPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.command.FriendshipEventCommand;
import com.yanceysong.im.common.model.common.ClientInfo;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupEntity;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupMemberEntity;
import com.yanceysong.im.domain.friendship.dao.mapper.ImFriendShipGroupMemberMapper;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.req.friend.DeleteFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.resp.AddGroupMemberResp;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupMemberService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupService;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ImFriendShipGroupMemberServiceImpl
 * @Description
 * @date 2023/5/5 11:05
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class ImFriendShipGroupMemberServiceImpl
        implements ImFriendShipGroupMemberService {
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;

    @Resource
    private ImFriendShipGroupService imFriendShipGroupService;

    @Resource
    private ImUserService imUserService;

    @Resource
    private ImFriendShipGroupMemberService thisService;

    @Override
    @Transactional
    public ResponseVO<AddGroupMemberResp> addGroupMember(AddFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService
                .getGroup(req.getSendId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(group.getCode(), group.getMsg());
        }
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        for (String receiverId : req.getReceiverIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(receiverId, req.getAppId());
            if (singleUserInfo.isOk()) {
                try {
                    ImFriendShipGroupEntity group1 = (ImFriendShipGroupEntity) group.getData();
                    int i = thisService.doAddGroupMember(group1.getGroupId(), receiverId);
                    if (i == 1) {
                        successId.add(receiverId);
                    }
                } catch (Exception e) {
                    errorId.add(receiverId);
                    e.printStackTrace();
                }
            }
        }
        AddGroupMemberResp resp = new AddGroupMemberResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        Long seq = imFriendShipGroupService.updateSeq(req.getSendId(), req.getGroupName(), req.getAppId());

        // 发送 TCP 通知
        AddFriendGroupMemberPack pack = new AddFriendGroupMemberPack();
        pack.setSendId(req.getSendId());
        pack.setGroupName(req.getGroupName());
        pack.setReceiverIds(successId);
        pack.setSequence(seq);
        messageProducer.sendToUserExceptClient(req.getSendId(), FriendshipEventCommand.FRIEND_GROUP_MEMBER_ADD,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<List<String>> delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendShipGroupEntity> group = imFriendShipGroupService.getGroup(req.getSendId(), req.getGroupName(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(group.getCode(), group.getMsg());
        }
        ArrayList<String> successId = new ArrayList<>();
        for (String receiverId : req.getReceiverIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(receiverId, req.getAppId());
            if (singleUserInfo.isOk()) {
                ImFriendShipGroupEntity group1 = group.getData();
                int i = deleteGroupMember(group1.getGroupId(), receiverId);
                if (i == 1) {
                    successId.add(receiverId);
                }
            }
        }
        Long seq = imFriendShipGroupService.updateSeq(req.getSendId(), req.getGroupName(), req.getAppId());

        // 发送 TCP 通知
        DeleteFriendGroupMemberPack pack = new DeleteFriendGroupMemberPack();
        pack.setSendId(req.getSendId());
        pack.setGroupName(req.getGroupName());
        pack.setReceiverIds(successId);
        pack.setSequence(seq);
        messageProducer.sendToUserExceptClient(req.getSendId(), FriendshipEventCommand.FRIEND_GROUP_MEMBER_DELETE,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return ResponseVO.successResponse(successId);
    }

    @Override
    public int doAddGroupMember(Long groupId, String receiverId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setReceiverId(receiverId);
        try {
            return imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int deleteGroupMember(Long groupId, String receiverId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId);
        queryWrapper.eq("receiver_id", receiverId);
        try {
            //            int insert = imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
            return imFriendShipGroupMemberMapper.delete(queryWrapper);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id", groupId);
        return imFriendShipGroupMemberMapper.delete(query);
    }
}

