package com.yanceysong.im.domain.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.friend.AllowFriendTypeEnum;
import com.yanceysong.im.common.enums.friend.CheckFriendShipTypeEnum;
import com.yanceysong.im.common.enums.friend.FriendShipErrorCode;
import com.yanceysong.im.common.enums.friend.FriendShipStatusEnum;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.common.model.RequestBase;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipEntity;
import com.yanceysong.im.domain.friendship.dao.mapper.ImFriendShipMapper;
import com.yanceysong.im.domain.friendship.model.req.*;
import com.yanceysong.im.domain.friendship.model.resp.CheckFriendShipResp;
import com.yanceysong.im.domain.friendship.model.resp.ImportFriendShipResp;
import com.yanceysong.im.domain.friendship.service.ImFriendService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipRequestService;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.service.ImUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private ImFriendShipRequestService imFriendShipRequestService;

    @Override
    public ResponseVO importFriendShip(ImportFriendShipReq req) {

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
            entity.setFromId(req.getFromId());
            try {
                int insert = imFriendShipMapper.insert(entity);
                if (insert == 1) {
                    successId.add(dto.getToId());
                } else {
                    errorId.add(dto.getToId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(dto.getToId());
            }
        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO addFriend(AddFriendReq req) {

        ResponseVO fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }
        ResponseVO toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }

        ImUserDataEntity data = (ImUserDataEntity) toInfo.getData();
        if (data.getFriendAllowType() != null && data.getFriendAllowType() == AllowFriendTypeEnum.NOT_NEED.getCode()) {
            // 被加用户未设置好友申请认证，直接走添加逻辑
            return this.doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
        } else {
            // 被加用户设置好友申请认证，走申请逻辑(im_friendship_request)
            QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getFromId());
            query.eq("to_id", req.getToItem().getToId());
            ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
            if (fromItem == null || fromItem.getStatus()
                    != FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                //插入一条好友申请的数据
                ResponseVO responseVO = imFriendShipRequestService.addFienshipRequest(req.getFromId(), req.getToItem(), req.getAppId());
                if (!responseVO.isOk()) {
                    return responseVO;
                }
            } else {
                return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_YOUR_FRIEND);
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO updateFriend(UpdateFriendReq req) {

        ResponseVO fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO toInfo = imUserService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }

        ResponseVO responseVO = this.doUpdate(req.getFromId(), req.getToItem(), req.getAppId());
        return responseVO;
    }

    @Transactional
    public ResponseVO doUpdate(String fromId, FriendDto dto, Integer appId) {
        UpdateWrapper<ImFriendShipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendShipEntity::getAddSource, dto.getAddSource())
                .set(ImFriendShipEntity::getExtra, dto.getExtra())
                .set(ImFriendShipEntity::getRemark, dto.getRemark())
                .eq(ImFriendShipEntity::getAppId, appId)
                .eq(ImFriendShipEntity::getToId, dto.getToId())
                .eq(ImFriendShipEntity::getFromId, fromId);

        int update = imFriendShipMapper.update(null, updateWrapper);
        if (update == 1) {
            return ResponseVO.successResponse();
        }

        return ResponseVO.errorResponse();
    }

    @Override
    @Transactional
    public ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId) {
        //A-B
        //Friend 表插入 A 和 B 两条记录
        //查询是否有记录存在，如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态

        // Friend 表插入 A 记录
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("to_id", dto.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if (fromItem == null) {
            //走添加逻辑。
            fromItem = new ImFriendShipEntity();
            fromItem.setAppId(appId);
            fromItem.setFromId(fromId);
//            entity.setToId(to);
            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }
        } else {
            //如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态
            if (fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
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
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
                }
            }

        }

        // Friend 表插入 B 记录
        QueryWrapper<ImFriendShipEntity> toQuery = new QueryWrapper<>();
        toQuery.eq("app_id", appId);
        toQuery.eq("from_id", dto.getToId());
        toQuery.eq("to_id", fromId);
        ImFriendShipEntity toItem = imFriendShipMapper.selectOne(toQuery);
        if (toItem == null) {
            toItem = new ImFriendShipEntity();
            toItem.setAppId(appId);
            toItem.setFromId(dto.getToId());
            BeanUtils.copyProperties(dto, toItem);
            toItem.setToId(fromId);
            toItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            toItem.setCreateTime(System.currentTimeMillis());
//            toItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
            int insert = imFriendShipMapper.insert(toItem);
        } else {
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() !=
                    toItem.getStatus()) {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                imFriendShipMapper.update(update, toQuery);
            }
        }
        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO deleteFriend(DeleteFriendReq req) {

        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if (fromItem == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.TO_IS_NOT_YOUR_FRIEND);
        } else {
            if (fromItem.getStatus() != null && fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
                imFriendShipMapper.update(update, query);

            } else {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteAllFriend(DeleteFriendReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        imFriendShipMapper.update(update, query);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getAllFriendShip(GetAllFriendShipReq req) {
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        return ResponseVO.successResponse(imFriendShipMapper.selectList(query));
    }

    @Override
    public ResponseVO getRelation(GetRelationReq req) {

        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());

        ImFriendShipEntity entity = imFriendShipMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.REPEATSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    @Override
    public ResponseVO checkBlck(CheckFriendShipReq req) {

        Map<String, Integer> toIdMap
                = req.getToIds().stream().collect(Collectors
                .toMap(Function.identity(), s -> 0));
        List<CheckFriendShipResp> result = new ArrayList<>();
        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            result = imFriendShipMapper.checkFriendShipBlack(req);
        } else {
            result = imFriendShipMapper.checkFriendShipBlackBoth(req);
        }

        Map<String, Integer> collect = result.stream()
                .collect(Collectors
                        .toMap(CheckFriendShipResp::getToId,
                                CheckFriendShipResp::getStatus));
        for (String toId :
                toIdMap.keySet()) {
            if (!collect.containsKey(toId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setStatus(toIdMap.get(toId));
                result.add(checkFriendShipResp);
            }
        }

        return ResponseVO.successResponse(result);
    }


    @Override
    public ResponseVO addBlack(AddFriendShipBlackReq req) {

        ResponseVO fromInfo = imUserService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO toInfo = imUserService.getSingleUserInfo(req.getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return toInfo;
        }
        QueryWrapper<ImFriendShipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());

        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(query);
        if (fromItem == null) {
            //走添加逻辑。
            fromItem = new ImFriendShipEntity();
            fromItem.setFromId(req.getFromId());
            fromItem.setToId(req.getToId());
            fromItem.setAppId(req.getAppId());
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = imFriendShipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.ADD_FRIEND_ERROR);
            }

        } else {
            //如果存在则判断状态，如果是拉黑，则提示已拉黑，如果是未拉黑，则修改状态
            if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
            } else {
                ImFriendShipEntity update = new ImFriendShipEntity();
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = imFriendShipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.ADD_BLACK_ERROR);
                }
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO deleteBlack(DeleteBlackReq req) {
        QueryWrapper<ImFriendShipEntity> queryFrom = new QueryWrapper<ImFriendShipEntity>()
                .eq("from_id", req.getFromId())
                .eq("app_id", req.getAppId())
                .eq("to_id", req.getToId());
        ImFriendShipEntity fromItem = imFriendShipMapper.selectOne(queryFrom);
        if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
            throw new YoungImException(FriendShipErrorCode.FRIEND_IS_NOT_YOUR_BLACK);
        }

        ImFriendShipEntity update = new ImFriendShipEntity();
        update.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int update1 = imFriendShipMapper.update(update, queryFrom);
        if (update1 == 1) {
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse();
    }

    @Override
    public ResponseVO checkFriendship(CheckFriendShipReq req) {
        Map<String, Integer> res = req.getToIds().stream()
                .collect(Collectors.toMap(
                        /* key: 每一个 toId */
                        Function.identity(),
                        /* value: 0 */
                        s -> 0));
        List<CheckFriendShipResp> resp;
        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            resp = imFriendShipMapper.checkFriendShip(req);
        } else {
            resp = imFriendShipMapper.checkFriendShipBoth(req);
        }

        Map<String, Integer> collect = resp.stream()
                .collect(Collectors.toMap(
                        /* key: 每一个 toId*/
                        CheckFriendShipResp::getToId,
                        /* value: 每一个 toId 与 fromId 的状态 */
                        CheckFriendShipResp::getStatus));

        for (String toId : res.keySet()) {
            // 如果没有出现 toId，将其填充
            if (!collect.containsKey(toId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setStatus(res.get(toId));
                resp.add(checkFriendShipResp);
            }
        }

        return ResponseVO.successResponse(resp);
    }
}
