package com.yanceysong.im.domain.friendship.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupEntity;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupMemberEntity;
import com.yanceysong.im.domain.friendship.dao.mapper.ImFriendShipGroupMemberMapper;
import com.yanceysong.im.domain.friendship.model.req.AddFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.req.DeleteFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.resp.AddGroupMemberResp;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupMemberService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupService;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.service.ImUserService;
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
    private ImFriendShipGroupMemberMapper imFriendShipGroupMemberMapper;

    @Resource
    private ImFriendShipGroupService imFriendShipGroupService;

    @Resource
    private ImUserService imUserService;

    @Resource
    private ImFriendShipGroupMemberService thisService;

    @Override
    @Transactional
    public ResponseVO addGroupMember(AddFriendShipGroupMemberReq req) {

        ResponseVO group = imFriendShipGroupService
                .getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                try {
                    ImFriendShipGroupEntity group1= (ImFriendShipGroupEntity) group.getData();
                    int i = thisService.doAddGroupMember(group1.getGroupId(), toId);
                    if(i == 1){
                        successId.add(toId);
                    }
                } catch (Exception e) {
                    errorId.add(toId);
                    e.printStackTrace();
                }
            }
        }
        AddGroupMemberResp resp = new AddGroupMemberResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ResponseVO group = imFriendShipGroupService
                .getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        ArrayList<String> list = new ArrayList<String>();
        for (String toId : req.getToIds()) {
            ResponseVO singleUserInfo = imUserService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                ImFriendShipGroupEntity group1= (ImFriendShipGroupEntity) group.getData();
                int i = deleteGroupMember(group1.getGroupId(), toId);
                if(i == 1){
                    list.add(toId);
                }
            }
        }
        return ResponseVO.successResponse(list);
    }

    @Override
    public int doAddGroupMember(Long groupId, String toId) {
        ImFriendShipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendShipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);
        try {
            return imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    public int deleteGroupMember(Long groupId, String toId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id",groupId);
        queryWrapper.eq("to_id",toId);
        try {
            //            int insert = imFriendShipGroupMemberMapper.insert(imFriendShipGroupMemberEntity);
            return imFriendShipGroupMemberMapper.delete(queryWrapper);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendShipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id",groupId);
        return imFriendShipGroupMemberMapper.delete(query);
    }
}

