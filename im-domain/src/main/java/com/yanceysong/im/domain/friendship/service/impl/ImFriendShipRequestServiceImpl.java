package com.yanceysong.im.domain.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanceysong.im.codec.pack.friend.ApproverFriendRequestPack;
import com.yanceysong.im.codec.pack.friend.ReadAllFriendRequestPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.enums.command.FriendshipEventCommand;
import com.yanceysong.im.common.enums.friend.ApproverFriendRequestStatusEnum;
import com.yanceysong.im.common.enums.friend.FriendShipErrorCode;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipRequestEntity;
import com.yanceysong.im.domain.friendship.dao.mapper.ImFriendShipRequestMapper;
import com.yanceysong.im.domain.friendship.model.req.friend.ApproverFriendRequestReq;
import com.yanceysong.im.domain.friendship.model.req.friend.FriendDto;
import com.yanceysong.im.domain.friendship.model.req.friend.ReadFriendShipRequestReq;
import com.yanceysong.im.domain.friendship.service.ImFriendService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipRequestService;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import com.yanceysong.im.infrastructure.utils.UserSequenceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName ImFriendShipRequestServiceImpl
 * @Description
 * @date 2023/5/5 11:06
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class ImFriendShipRequestServiceImpl implements ImFriendShipRequestService {
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private ImFriendShipRequestMapper imFriendShipRequestMapper;

    @Resource
    private ImFriendService imFriendShipService;
    @Resource
    private RedisSequence redisSequence;

    @Resource
    private UserSequenceRepository userSequenceRepository;
    @Override
    public ResponseVO getFriendRequest(String fromId, Integer appId) {
        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("to_id", fromId);
        List<ImFriendShipRequestEntity> requestList = imFriendShipRequestMapper.selectList(query);
        return ResponseVO.successResponse(requestList);
    }


    //A + B
    @Override
    public ResponseVO<ResponseVO.NoDataReturn> addFienshipRequest(String fromId, FriendDto dto, Integer appId) {
        QueryWrapper<ImFriendShipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", appId);
        queryWrapper.eq("from_id", fromId);
        queryWrapper.eq("to_id", dto.getToId());
        ImFriendShipRequestEntity request = imFriendShipRequestMapper.selectOne(queryWrapper);
        long seq = redisSequence.doGetSeq(appId + ":" +
                SeqConstants.FRIEND_SHIP_REQUEST_SEQ);

        if (request == null) {
            // 第一次添加，插入申请结果
            request = new ImFriendShipRequestEntity();
            request.setAddSource(dto.getAddSource());
            request.setAddWording(dto.getAddWording());
            request.setAppId(appId);
            request.setFromId(fromId);
            request.setToId(dto.getToId());
            request.setReadStatus(0);
            request.setSequence(seq);
            request.setApproveStatus(0);
            request.setRemark(dto.getRemark());
            request.setCreateTime(System.currentTimeMillis());
            imFriendShipRequestMapper.insert(request);
        } else {
            //修改记录内容和更新时间
            if(StringUtils.isNotBlank(dto.getAddSource())){
                request.setAddWording(dto.getAddWording());
            }
            //修改记录内容和更新时间
            if (StringUtils.isNotBlank(dto.getRemark())) {
                request.setRemark(dto.getRemark());
            }
            if (StringUtils.isNotBlank(dto.getAddWording())) {
                request.setAddWording(dto.getAddWording());
            }
            request.setSequence(seq);
            request.setApproveStatus(0);
            request.setReadStatus(0);
            imFriendShipRequestMapper.updateById(request);
        }
        userSequenceRepository.writeUserSeq(appId, dto.getToId(), SeqConstants.FRIEND_SHIP_REQUEST_SEQ, seq);

        //发送好友申请的 tcp 给接收方
        messageProducer.sendToUserAllClient(dto.getToId(), FriendshipEventCommand.FRIEND_REQUEST, request, appId);

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO approverFriendRequest(ApproverFriendRequestReq req) {

        ImFriendShipRequestEntity imFriendShipRequestEntity = imFriendShipRequestMapper.selectById(req.getId());
        if (imFriendShipRequestEntity == null) {
            throw new YoungImException(FriendShipErrorCode.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if (!req.getOperator().equals(imFriendShipRequestEntity.getToId())) {
            //只能审批发给自己的好友请求
            throw new YoungImException(FriendShipErrorCode.NOT_APPROVER_OTHER_MAN_REQUEST);
        }
        long seq = redisSequence.doGetSeq(req.getAppId() +
                ":" + SeqConstants.FRIEND_SHIP_REQUEST_SEQ);

        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setApproveStatus(req.getStatus());
        update.setUpdateTime(System.currentTimeMillis());
        update.setId(req.getId());
        update.setSequence(seq);
        imFriendShipRequestMapper.updateById(update);
        userSequenceRepository.writeUserSeq(req.getAppId(),
                req.getOperator(), SeqConstants.FRIEND_SHIP_REQUEST_SEQ, seq);

        if (ApproverFriendRequestStatusEnum.AGREE.getCode() == req.getStatus()) {
            //同意 ===> 去执行添加好友逻辑
            FriendDto dto = new FriendDto();
            dto.setAddSource(imFriendShipRequestEntity.getAddSource());
            dto.setAddWording(imFriendShipRequestEntity.getAddWording());
            dto.setRemark(imFriendShipRequestEntity.getRemark());
            dto.setToId(imFriendShipRequestEntity.getToId());
            ResponseVO responseVO = imFriendShipService.doAddFriend(req, imFriendShipRequestEntity.getFromId(), dto, req.getAppId());
//            if(!responseVO.isOk()){
////                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
//                return responseVO;
//            }
            if (!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCode.TO_IS_YOUR_FRIEND.getCode()) {
                return responseVO;
            }
        }
        //发送好友申请的 tcp 给接收方
        ApproverFriendRequestPack approverFriendRequestPack = new ApproverFriendRequestPack();
        approverFriendRequestPack.setId(req.getId());
        approverFriendRequestPack.setStatus(req.getStatus());
        approverFriendRequestPack.setSequence(seq);
        messageProducer.sendMsgToUser(imFriendShipRequestEntity.getToId(), FriendshipEventCommand.FRIEND_REQUEST_APPROVER,
                approverFriendRequestPack, req.getAppId(), req.getClientType(), req.getImei());
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> readFriendShipRequestReq(ReadFriendShipRequestReq req) {
        QueryWrapper<ImFriendShipRequestEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("to_id", req.getFromId());
        long seq = redisSequence.doGetSeq(req.getAppId() + ":" +
                SeqConstants.FRIEND_SHIP_REQUEST_SEQ);

        ImFriendShipRequestEntity update = new ImFriendShipRequestEntity();
        update.setReadStatus(1);
        imFriendShipRequestMapper.update(update, query);
        // TCP 通知
        ReadAllFriendRequestPack readAllFriendRequestPack = new ReadAllFriendRequestPack();
        readAllFriendRequestPack.setFromId(req.getFromId());
        messageProducer.sendMsgToUser(req.getFromId(), FriendshipEventCommand.FRIEND_REQUEST_READ,
                readAllFriendRequestPack, req.getAppId(), req.getClientType(), req.getImei());

        return ResponseVO.successResponse();
    }

}

