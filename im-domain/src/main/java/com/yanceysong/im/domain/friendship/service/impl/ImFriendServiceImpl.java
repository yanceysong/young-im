package com.yanceysong.im.domain.friendship.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yanceysong.im.codec.pack.friend.*;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.CallbackCommand;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.enums.command.FriendshipEventCommand;
import com.yanceysong.im.common.enums.friend.AllowFriendTypeEnum;
import com.yanceysong.im.common.enums.friend.CheckFriendShipTypeEnum;
import com.yanceysong.im.common.enums.friend.FriendShipErrorCode;
import com.yanceysong.im.common.enums.friend.FriendShipStatusEnum;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.common.model.common.RequestBase;
import com.yanceysong.im.common.model.sync.SyncReq;
import com.yanceysong.im.common.model.sync.SyncResp;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipEntity;
import com.yanceysong.im.domain.friendship.dao.mapper.ImFriendShipMapper;
import com.yanceysong.im.domain.friendship.model.callback.AddFriendAfterCallbackDto;
import com.yanceysong.im.domain.friendship.model.callback.AddFriendBlackAfterCallbackDto;
import com.yanceysong.im.domain.friendship.model.callback.DeleteFriendAfterCallbackDto;
import com.yanceysong.im.domain.friendship.model.req.friend.*;
import com.yanceysong.im.domain.friendship.model.resp.CheckFriendShipResp;
import com.yanceysong.im.domain.friendship.model.resp.ImportFriendShipResp;
import com.yanceysong.im.domain.friendship.service.ImFriendService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipRequestService;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.callback.CallbackService;
import com.yanceysong.im.infrastructure.config.AppConfig;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import com.yanceysong.im.infrastructure.utils.UserSequenceRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName ImFriendServiceImpl
 * @Description
 * @date 2023/5/5 11:04
 * @Author yanceysong
 * @Version 1.0
 */

@Service
public class ImFriendServiceImpl implements ImFriendService {

    @Resource
    private ImFriendShipMapper imFriendShipMapper;

    @Resource
    private ImUserService imUserService;

    @Resource
    private ImFriendService imFriendService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private CallbackService callbackService;
    @Resource
    private RedisSequence redisSequence;

    @Resource
    private UserSequenceRepository userSequenceRepository;
    @Resource
    private ImFriendShipRequestService imFriendShipRequestService;

    @Override
    public ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req) {
        if (req.getFriendItem().size() > 100) {
            return ResponseVO.errorResponse(FriendShipErrorCode.IMPORT_SIZE_BEYOND);
        }
        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImportFriendShipReq.ImportFriendDto dto : req.getFriendItem()) {
            ImFriendShipEntity entity = new ImFriendShipEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setAppId(req.getAppId());
            entity.setSendId(req.getSendId());
            try {
                int insert = imFriendShipMapper.insert(entity);
                if (insert == 1) {
                    successId.add(dto.getReceiverId());
                } else {
                    errorId.add(dto.getReceiverId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(dto.getReceiverId());
            }
        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO addFriend(AddFriendReq req) {
        //from userInfo
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getSendId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return ResponseVO.errorResponse(fromInfo.getCode(), fromInfo.getMsg());
        }
        // to userInfo
        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToItem().getReceiverId(), req.getAppId());
        if (!toInfo.isOk()) {
            return ResponseVO.errorResponse(toInfo.getCode(), toInfo.getMsg());
        }

        // 事件执行前且选择开启回调
        if (appConfig.isAddFriendBeforeCallback()) {
            ResponseVO responseVO = callbackService.beforeCallback(req.getAppId(), CallbackCommand.ADD_FRIEND_AFTER, JSONObject.toJSONString(req));
            if (!responseVO.isOk()) {
                // 如果回调不成功(状态码非 200), 错误需要返回给前端
                // 注意: 这里的回调不成功是指响应失败，表明用户没有该权限。
                // 回调机制抛出异常需要放行，正常处理，表名服务器后台故障，需要维修
                return responseVO;
            }
        }

        //拿到 to 的用户信息
        ImUserDataEntity data = toInfo.getData();
        if (data.getFriendAllowType() != null && data.getFriendAllowType() == AllowFriendTypeEnum.NOT_NEED.getCode()) {
            // 被加用户未设置好友申请认证，直接走添加逻辑
            return this.doAddFriend(req, req.getSendId(), req.getToItem(), req.getAppId());
        } else {
            // 被加用户设置好友申请认证，走申请逻辑(im_friendship_request)
            QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getSendId());
            query.eq("to_id", req.getToItem().getReceiverId());
            ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
            if (fromItem == null || !Objects.equals(fromItem.getStatus(), FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                //插入一条好友申请的数据
                ResponseVO<ResponseVO.NoDataReturn> responseVO = imFriendShipRequestService.addFienshipRequest(req.getSendId(), req.getToItem(), req.getAppId());
                if (!responseVO.isOk()) {
                    return responseVO;
                }
            } else {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
        }
        return ResponseVO.successResponse(data);
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> updateFriend(UpdateFriendReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getSendId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return ResponseVO.errorResponse(fromInfo.getCode(), fromInfo.getMsg());
        }
        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getToItem().getReceiverId(), req.getAppId());
        if (!toInfo.isOk()) {
            return ResponseVO.errorResponse(toInfo.getCode(), toInfo.getMsg());
        }
        ResponseVO<ResponseVO.NoDataReturn> responseVO = doUpdate(req.getSendId(), req.getToItem(), req.getAppId());
        if (responseVO.isOk()) {
            // 发送事件消息
            UpdateFriendPack updateFriendPack = new UpdateFriendPack();
            updateFriendPack.setRemark(req.getToItem().getRemark());
            updateFriendPack.setReceiverId(req.getToItem().getReceiverId());
            messageProducer.sendMsgToUser(req.getSendId(), FriendshipEventCommand.FRIEND_UPDATE, updateFriendPack,
                    req.getAppId(), req.getClientType(), req.getImei());
            //回调 callback
            if (appConfig.isModifyFriendAfterCallback()) {
                AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
                callbackDto.setSendId(req.getSendId());
                callbackDto.setToItem(req.getToItem());
                callbackService.beforeCallback(req.getAppId(),
                        CallbackCommand.UPDATE_FRIEND_AFTER, JSONObject
                                .toJSONString(callbackDto));
            }
        }
        return responseVO;
    }

    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> doUpdate(String sendId, FriendDto dto, Integer appId) {
        long seq = redisSequence.doGetSeq(appId + ":" + SeqConstants.FRIEND_SHIP_SEQ);

        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, dto.getAddSource()).set(ImFriendShipEntity::getExtra, dto.getExtra()).set(ImFriendShipEntity::getRemark, dto.getRemark()).eq(ImFriendShipEntity::getAppId, appId).eq(ImFriendShipEntity::getReceiverId, dto.getReceiverId()).eq(ImFriendShipEntity::getSendId, sendId);

        int update = imFriendShipMapper.update(null, updateWrapper);
        if (update == 1) {
            userSequenceRepository.writeUserSeq(appId, sendId, SeqConstants.FRIEND_SHIP_SEQ, seq);

            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse();
    }

    @Override
    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> doAddFriend(RequestBase requestBase, String sendId, FriendDto dto, Integer appId) {
        //A-B
        //Friend 表插入 A 和 B 两条记录
        //查询是否有记录存在，如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态

        // Friend 表插入 A 记录
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", sendId);
        query.eq("to_id", dto.getReceiverId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        long seq = 0L;
        if (fromItem == null) {
            //走添加逻辑。
            fromItem = getFriendShipEntity(appId, sendId, dto.getReceiverId(), dto);
            seq = redisSequence.doGetSeq(appId + ":" + SeqConstants.FRIEND_SHIP_SEQ);
            fromItem.setFriendSequence(seq);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
            userSequenceRepository.writeUserSeq(appId, sendId, SeqConstants.FRIEND_SHIP_SEQ, seq);

        } else {
            //如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态
            if (fromItem.getStatus().equals(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            } else {
                ImFriendShipEntity update = new ImFriendShipEntity();
                if (StringUtils.isNotBlank(dto.getAddSource())) {
                    update.setAddSource(dto.getAddSource());
                }
                if (StringUtils.isNotBlank(dto.getRemark())) {
                    update.setRemark(dto.getRemark());
                }
                if (StringUtils.isNotBlank(dto.getExtra())) {
                    update.setExtra(dto.getExtra());
                }
                seq = redisSequence.doGetSeq(appId + ":" + SeqConstants.FRIEND_SHIP_SEQ);
                update.setFriendSequence(seq);
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
                userSequenceRepository.writeUserSeq(appId, sendId, SeqConstants.FRIEND_SHIP_SEQ, seq);

            }
        }
        // Friend 表插入 B 记录
        QueryWrapper<ImFriendShipEntity> toQuery = new QueryWrapper<>();
        toQuery.eq("app_id", appId);
        toQuery.eq("from_id", dto.getReceiverId());
        toQuery.eq("to_id", sendId);
        ImFriendShipEntity toItem = imFriendShipMapper.selectOne(toQuery);
        if (toItem == null) {
            toItem = getFriendShipEntity(appId, dto.getReceiverId(), sendId, dto);
            toItem.setFriendSequence(seq);
            int insert = imFriendShipMapper.insert(toItem);
            userSequenceRepository.writeUserSeq(appId, dto.getReceiverId(), SeqConstants.FRIEND_SHIP_SEQ, seq);

        } else {
            if (!Objects.equals(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode(), toItem.getStatus())) {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setFriendSequence(seq);
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                imFriendShipMapper.update(update, toQuery);
                userSequenceRepository.writeUserSeq(appId, dto.getReceiverId(), SeqConstants.FRIEND_SHIP_SEQ, seq);

            }
        }
        // TCP 通知发送给 from 端
        AddFriendPack addFriendPack = new AddFriendPack();
        addFriendPack.setSendId(fromItem.getSendId());
        addFriendPack.setRemark(fromItem.getRemark());
        addFriendPack.setReceiverId(fromItem.getReceiverId());
        addFriendPack.setAddSource(fromItem.getAddSource());
        addFriendPack.setSequence(seq);
        if (requestBase != null) {
            // 存在 req 同步除本端的所有端
            messageProducer.sendMsgToUser(sendId, FriendshipEventCommand.FRIEND_ADD, addFriendPack,
                    requestBase.getAppId(), requestBase.getClientType(), requestBase.getImei());
        } else {
            // 没有 req 直接同步到所有端
            messageProducer.sendToUserAllClient(sendId,
                    FriendshipEventCommand.FRIEND_ADD, addFriendPack, appId);
        }

        // TCP 通知发给 to 端
        AddFriendPack addFriendToPack = new AddFriendPack();
        addFriendToPack.setSendId(toItem.getSendId());
        addFriendToPack.setRemark(toItem.getRemark());
        addFriendToPack.setReceiverId(toItem.getReceiverId());
        addFriendToPack.setAddSource(toItem.getAddSource());
        addFriendToPack.setSequence(seq);
        // 同步所有端
        messageProducer.sendToUserAllClient(toItem.getSendId(),
                FriendshipEventCommand.FRIEND_ADD, addFriendToPack, appId);

        // 事件执行后且选择开启回调
        if (appConfig.isAddFriendAfterCallback()) {
            AddFriendAfterCallbackDto callbackDto = new AddFriendAfterCallbackDto();
            callbackDto.setSendId(sendId);
            callbackDto.setToItem(dto);
            callbackService.afterCallback(appId, CallbackCommand.ADD_FRIEND_AFTER, JSONObject.toJSONString(callbackDto));
        }
        return ResponseVO.successResponse();
    }

    private ImFriendShipEntity getFriendShipEntity(Integer appId, String sendId, String receiverId, FriendDto dto) {
        ImFriendShipEntity userItem = new ImFriendShipEntity();
        userItem.setAppId(appId);
        userItem.setSendId(sendId);
        userItem.setReceiverId(receiverId);
        userItem.setRemark(dto.getRemark());
        userItem.setAddSource(dto.getAddSource());
        userItem.setExtra(dto.getExtra());
        userItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        userItem.setCreateTime(System.currentTimeMillis());
        return userItem;
    }


    @Override
    public ResponseVO<ResponseVO.NoDataReturn> deleteFriend(DeleteFriendReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getSendId());
        query.eq("to_id", req.getReceiverId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if (fromItem == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        } else {
            if (fromItem.getStatus() != null &&
                    Objects.equals(fromItem.getStatus(),
                            FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode())) {

                ImFriendShipEntity update = new ImFriendShipEntity();
                long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIEND_SHIP_SEQ);
                update.setFriendSequence(seq);
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
                imFriendShipMapper.update(update, query);
                userSequenceRepository.writeUserSeq(req.getAppId(), req.getSendId(), SeqConstants.FRIEND_SHIP_SEQ, seq);

                // TCP 通知
                DeleteFriendPack deleteFriendPack = new DeleteFriendPack();
                deleteFriendPack.setSendId(req.getSendId());
                deleteFriendPack.setReceiverId(req.getReceiverId());
                deleteFriendPack.setSequence(seq);
                messageProducer.sendMsgToUser(req.getSendId(), FriendshipEventCommand.FRIEND_DELETE, deleteFriendPack,
                        req.getAppId(), req.getClientType(), req.getImei());

                //之后回调
                if (appConfig.isAddFriendAfterCallback()) {
                    DeleteFriendAfterCallbackDto callbackDto = new DeleteFriendAfterCallbackDto();
                    callbackDto.setSendId(req.getSendId());
                    callbackDto.setReceiverId(req.getReceiverId());
                    callbackService.afterCallback(req.getAppId(),
                            CallbackCommand.DELETE_FRIEND_AFTER,
                            JSONObject.toJSONString(callbackDto));
                }

            } else {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> deleteAllFriend(DeleteFriendReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getSendId());
        query.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, query);
        //消息事件发送
        DeleteAllFriendPack deleteFriendPack = new DeleteAllFriendPack();
        deleteFriendPack.setSendId(req.getSendId());
        messageProducer.sendMsgToUser(req.getSendId(), FriendshipEventCommand.FRIEND_ALL_DELETE,
                deleteFriendPack, req.getAppId(), req.getClientType(), req.getImei());

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<List<ImFriendShipEntity>> getAllFriendShip(GetAllFriendShipReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getSendId());
        return ResponseVO.successResponse(imFriendShipMapper.selectList(query));
    }

    @Override
    public ResponseVO<ImFriendShipEntity> getRelation(GetRelationReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getSendId());
        query.eq("to_id", req.getReceiverId());
        ImFriendShipEntity entity = imFriendShipMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.REPEATSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    @Override
    public ResponseVO<List<CheckFriendShipResp>> checkBlck(CheckFriendShipReq req) {
        Map<String, Integer> receiverIdMap = req.getReceiverIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));
        List<CheckFriendShipResp> result = new ArrayList<>();
        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            result = imFriendShipMapper.checkFriendShipBlack(req);
        } else {
            result = imFriendShipMapper.checkFriendShipBlackBoth(req);
        }
        Map<String, Integer> collect = result.stream().collect(Collectors.toMap(CheckFriendShipResp::getReceiverId, CheckFriendShipResp::getStatus));
        for (String receiverId : receiverIdMap.keySet()) {
            if (!collect.containsKey(receiverId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setReceiverId(receiverId);
                checkFriendShipResp.setSendId(req.getSendId());
                checkFriendShipResp.setStatus(receiverIdMap.get(receiverId));
                result.add(checkFriendShipResp);
            }
        }
        return ResponseVO.successResponse(result);
    }


    @Override
    public ResponseVO<ResponseVO.NoDataReturn> addBlack(AddFriendShipBlackReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = imUserService.getSingleUserInfo(req.getSendId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return ResponseVO.errorResponse(fromInfo.getCode(), fromInfo.getMsg());
        }
        ResponseVO<ImUserDataEntity> toInfo = imUserService.getSingleUserInfo(req.getReceiverId(), req.getAppId());
        if (!toInfo.isOk()) {
            return ResponseVO.errorResponse(toInfo.getCode(), toInfo.getMsg());
        }
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getSendId());
        query.eq("to_id", req.getReceiverId());
        long seq = 0L;
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if (fromItem == null) {
            //走添加逻辑。
            seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIEND_SHIP_SEQ);

            fromItem = new ImFriendShipEntity();
            fromItem.setSendId(req.getSendId());
            fromItem.setReceiverId(req.getReceiverId());
            fromItem.setAppId(req.getAppId());
            fromItem.setFriendSequence(seq);
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
            userSequenceRepository.writeUserSeq(req.getAppId(), req.getSendId(), SeqConstants.FRIEND_SHIP_SEQ, seq);

        } else {
            //如果存在则判断状态，如果是拉黑，则提示已拉黑，如果是未拉黑，则修改状态
            if (fromItem.getBlack() != null && Objects.equals(fromItem.getBlack(), FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode())) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
            } else {
                seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIEND_SHIP_SEQ);

                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
                userSequenceRepository.writeUserSeq(req.getAppId(), req.getSendId(), SeqConstants.FRIEND_SHIP_SEQ, seq);

            }
        }
        // 发送 TCP 通知
        AddFriendBlackPack addFriendBlackPack = new AddFriendBlackPack();
        addFriendBlackPack.setSendId(req.getSendId());
        addFriendBlackPack.setReceiverId(req.getReceiverId());
        addFriendBlackPack.setSequence(seq);
        messageProducer.sendMsgToUser(req.getSendId(), FriendshipEventCommand.FRIEND_BLACK_ADD, addFriendBlackPack,
                req.getAppId(), req.getClientType(), req.getImei());

        //之后回调
        if (appConfig.isAddFriendShipBlackAfterCallback()) {
            AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
            callbackDto.setSendId(req.getSendId());
            callbackDto.setReceiverId(req.getReceiverId());
            callbackService.afterCallback(req.getAppId(), CallbackCommand.ADD_BLACK_AFTER, JSONObject.toJSONString(callbackDto));
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> deleteBlack(DeleteBlackReq req) {
        QueryWrapper<ImFriendShipEntity> queryFrom = new QueryWrapper<ImFriendShipEntity>()
                .eq("from_id", req.getSendId())
                .eq("app_id", req.getAppId())
                .eq("to_id", req.getReceiverId());
        long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.FRIEND_SHIP_SEQ);

        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(queryFrom);
        if (fromItem.getBlack() != null && Objects.equals(fromItem.getBlack(), FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode())) {
            throw new YoungImException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int update1 = imFriendShipMapper.update(update, queryFrom);
        if (update1 == 1) {
            userSequenceRepository.writeUserSeq(req.getAppId(), req.getSendId(), SeqConstants.FRIEND_SHIP_SEQ, seq);

            // 发送 TCP 通知
            DeleteBlackPack deleteFriendPack = new DeleteBlackPack();
            deleteFriendPack.setSendId(req.getSendId());
            deleteFriendPack.setReceiverId(req.getReceiverId());
            deleteFriendPack.setSequence(seq);
            messageProducer.sendMsgToUser(req.getSendId(), FriendshipEventCommand.FRIEND_BLACK_DELETE,
                    deleteFriendPack, req.getAppId(), req.getClientType(), req.getImei());

            //之后回调
            if (appConfig.isAddFriendShipBlackAfterCallback()) {
                AddFriendBlackAfterCallbackDto callbackDto = new AddFriendBlackAfterCallbackDto();
                callbackDto.setSendId(req.getSendId());
                callbackDto.setReceiverId(req.getReceiverId());
                callbackService.afterCallback(req.getAppId(),
                        CallbackCommand.DELETE_BLACK, JSONObject
                                .toJSONString(callbackDto));
            }
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse();
    }

    @Override
    public ResponseVO<List<CheckFriendShipResp>> checkFriendship(CheckFriendShipReq req) {
        Map<String, Integer> res = req.getReceiverIds()
                .stream()
                .collect(Collectors.toMap(                /* key: 每一个 receiverId */
                        Function.identity(),
                        /* value: 0 */
                        s -> 0));
        List<CheckFriendShipResp> resp;
        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            resp = imFriendShipMapper.checkFriendShip(req);
        } else {
            resp = imFriendShipMapper.checkFriendShipBoth(req);
        }
        Map<String, Integer> collect = resp.stream().collect(Collectors.toMap(
                /* key: 每一个 receiverId*/
                CheckFriendShipResp::getReceiverId,
                /* value: 每一个 receiverId 与 sendId 的状态 */
                CheckFriendShipResp::getStatus));
        for (String receiverId : res.keySet()) {
            // 如果没有出现 receiverId，将其填充
            if (!collect.containsKey(receiverId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setSendId(req.getSendId());
                checkFriendShipResp.setReceiverId(receiverId);
                checkFriendShipResp.setStatus(res.get(receiverId));
                resp.add(checkFriendShipResp);
            }
        }
        return ResponseVO.successResponse(resp);
    }
    @Override
    public List<String> getAllFriendId(String userId, Integer appId) {
        return imFriendShipMapper.getAllFriendId(userId,appId);
    }
    @Override
    public ResponseVO syncFriendShipList(SyncReq req) {
        if (req.getMaxLimit() > appConfig.getFriendShipMaxCount()) {
            // 前端传输限制，保证一次增量拉取数据量不超过配置文件的值
            req.setMaxLimit(appConfig.getFriendShipMaxCount());
        }

        SyncResp<ImFriendShipEntity> resp = new SyncResp<>();
        // server_seq > req(client)_seq limit maxLimit;
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("from_id", req.getOperator());
        query.gt("friend_sequence", req.getLastSequence());
        query.eq("app_id", req.getAppId());
        query.last("limit " + req.getMaxLimit());
        query.orderByAsc("friend_sequence");
        List<ImFriendShipEntity> list = imFriendShipMapper.selectList(query);
        if (!CollectionUtils.isEmpty(list)) {
            ImFriendShipEntity maxSeqEntity = list.get(list.size() - 1);
            resp.setDataList(list);
            // 设置最大 Seq
            Long friendShipMaxSeq = imFriendShipMapper
                    .getFriendShipMaxSeq(req.getAppId());
            resp.setMaxSequence(friendShipMaxSeq);
            // 设置是否拉取完毕
            resp.setCompleted(maxSeqEntity.getFriendSequence() >= friendShipMaxSeq);
            return ResponseVO.successResponse(resp);
        }
        resp.setCompleted(true);
        return ResponseVO.successResponse();
    }
}
