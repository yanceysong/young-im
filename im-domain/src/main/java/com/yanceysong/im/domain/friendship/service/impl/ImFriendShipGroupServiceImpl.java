package com.yanceysong.im.domain.friendship.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.friend.DelFlagEnum;
import com.yanceysong.im.common.enums.friend.FriendShipErrorCode;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupEntity;
import com.yanceysong.im.domain.friendship.dao.mapper.ImFriendShipGroupMapper;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.model.req.friend.DeleteFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupMemberService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupService;
import com.yanceysong.im.domain.user.service.ImUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ImFriendShipGroupMapper imFriendShipGroupMapper;

    @Resource
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;

    @Resource
    private ImUserService imUserService;

    @Override
    @Transactional
    public ResponseVO addGroup(AddFriendShipGroupReq req) {

        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", req.getGroupName());
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);

        if (entity != null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        //写入db
        ImFriendShipGroupEntity insert = new ImFriendShipGroupEntity();
        insert.setAppId(req.getAppId());
        insert.setCreateTime(System.currentTimeMillis());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insert.setGroupName(req.getGroupName());
        insert.setFromId(req.getFromId());
        try {
            int insert1 = imFriendShipGroupMapper.insert(insert);

            if (insert1 != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }

            if (CollectionUtil.isNotEmpty(req.getToIds())) {
                AddFriendShipGroupMemberReq addFriendShipGroupMemberReq = new AddFriendShipGroupMemberReq();
                addFriendShipGroupMemberReq.setFromId(req.getFromId());
                addFriendShipGroupMemberReq.setGroupName(req.getGroupName());
                addFriendShipGroupMemberReq.setToIds(req.getToIds());
                addFriendShipGroupMemberReq.setAppId(req.getAppId());
                imFriendShipGroupMemberService.addGroupMember(addFriendShipGroupMemberReq);
                return ResponseVO.successResponse();
            }
        } catch (DuplicateKeyException e) {
            e.getStackTrace();
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_EXIST);
        }
        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO deleteGroup(DeleteFriendShipGroupReq req) {

        for (String groupName : req.getGroupName()) {
            QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
            query.eq("group_name", groupName);
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getFromId());
            query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);
            if (entity != null) {
                ImFriendShipGroupEntity update = new ImFriendShipGroupEntity();
                update.setGroupId(entity.getGroupId());
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                imFriendShipGroupMapper.updateById(update);
                imFriendShipGroupMemberService.clearGroupMember(entity.getGroupId());
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO getGroup(String fromId, String groupName, Integer appId) {
        QueryWrapper<ImFriendShipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendShipGroupEntity entity = imFriendShipGroupMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

}
