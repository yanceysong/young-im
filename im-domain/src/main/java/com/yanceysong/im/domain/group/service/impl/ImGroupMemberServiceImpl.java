package com.yanceysong.im.domain.group.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yanceysong.im.codec.pack.group.AddGroupMemberPack;
import com.yanceysong.im.codec.pack.group.GroupMemberSpeakPack;
import com.yanceysong.im.codec.pack.group.UpdateGroupMemberPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.CallbackCommand;
import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.enums.group.GroupErrorCode;
import com.yanceysong.im.common.enums.group.GroupMemberRoleEnum;
import com.yanceysong.im.common.enums.group.GroupStatusEnum;
import com.yanceysong.im.common.enums.group.GroupTypeEnum;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.common.model.ClientInfo;
import com.yanceysong.im.domain.group.GroupMessageProducer;
import com.yanceysong.im.domain.group.dao.ImGroupEntity;
import com.yanceysong.im.domain.group.dao.ImGroupMemberEntity;
import com.yanceysong.im.domain.group.dao.mapper.ImGroupMemberMapper;
import com.yanceysong.im.domain.group.model.req.callback.AddMemberAfterCallback;
import com.yanceysong.im.domain.group.model.req.group.*;
import com.yanceysong.im.domain.group.model.resp.AddMemberResp;
import com.yanceysong.im.domain.group.model.resp.GetRoleInGroupResp;
import com.yanceysong.im.domain.group.service.ImGroupMemberService;
import com.yanceysong.im.domain.group.service.ImGroupService;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.callback.CallbackService;
import com.yanceysong.im.infrastructure.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @ClassName ImGroupMemberServiceImpl
 * @Description
 * @date 2023/5/5 11:51
 * @Author yanceysong
 * @Version 1.0
 */
@Service
@Slf4j
public class ImGroupMemberServiceImpl implements ImGroupMemberService {
    @Resource
    private GroupMessageProducer groupMessageProducer;
    @Resource
    private ImGroupMemberMapper imGroupMemberMapper;

    @Resource
    private ImGroupService groupService;

    @Resource
    private ImGroupMemberService groupMemberService;

    @Resource
    private ImUserService imUserService;
    @Resource
    private CallbackService callbackService;

    @Resource
    private AppConfig appConfig;

    @Override
    public ResponseVO<List<AddMemberResp>> importGroupMember(ImportGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();
        // 查看是否存在目标群组
        ResponseVO<ImGroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(groupResp.getCode(), groupResp.getMsg());
        }
        for (GroupMemberDto memberId : req.getMembers()) {
            ResponseVO responseVO;
            try {
                responseVO = groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                e.printStackTrace();
                responseVO = ResponseVO.errorResponse();
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(memberId.getMemberId());
            if (responseVO.isOk()) {
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
            } else {
                addMemberResp.setResult(1);
            }
            resp.add(addMemberResp);
        }
        return ResponseVO.successResponse(resp);
    }

    /**
     * 添加群成员，内部调用
     *
     * @return
     */
    @Override
    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> addGroupMember(String groupId, Integer appId, GroupMemberDto dto) {
        ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(dto.getMemberId(), appId);
        if (!singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(singleUserInfo.getCode(), singleUserInfo.getMsg());
        }
        // 查询是否有群主
        if (dto.getRole() != null && GroupMemberRoleEnum.OWNER.getCode().equals(dto.getRole())) {
            QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
            queryOwner.eq("group_id", groupId);
            queryOwner.eq("app_id", appId);
            queryOwner.eq("role", GroupMemberRoleEnum.OWNER.getCode());
            Long ownerNum = imGroupMemberMapper.selectCount(queryOwner);
            if (ownerNum > 0) {
                return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_HAVE_OWNER);
            }
        }

        QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id", groupId);
        query.eq("app_id", appId);
        query.eq("member_id", dto.getMemberId());
        ImGroupMemberEntity memberDto = imGroupMemberMapper.selectOne(query);

        long now = System.currentTimeMillis();
        if (memberDto == null) {
            //初次加群
            memberDto = new ImGroupMemberEntity();
///            BeanUtils.copyProperties(dto, memberDto);
            memberDto.setGroupId(groupId);
            memberDto.setAppId(appId);
            memberDto.setJoinTime(now);
            memberDto.setJoinTime(dto.getJoinTime());
            memberDto.setJoinType(dto.getJoinType());
            int insert = imGroupMemberMapper.insert(memberDto);
            if (insert == 1) {
                return ResponseVO.successResponse();
            }
            return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
        } else if (GroupMemberRoleEnum.LEAVE.getCode().equals(memberDto.getRole())) {
            //重新进群
            memberDto = new ImGroupMemberEntity();
            memberDto.setMemberId(dto.getMemberId());
            memberDto.setRole(dto.getRole());
            memberDto.setSpeakDate(dto.getSpeakDate());
            memberDto.setAlias(dto.getAlias());
            memberDto.setJoinTime(dto.getJoinTime());
            memberDto.setJoinType(dto.getJoinType());
            memberDto.setGroupId(groupId);
            memberDto.setAppId(appId);
            memberDto.setJoinTime(now);
            int update = imGroupMemberMapper.update(memberDto, query);
            if (update == 1) {
                return ResponseVO.successResponse();
            }
            return ResponseVO.errorResponse(GroupErrorCode.USER_JOIN_GROUP_ERROR);
        }

        return ResponseVO.errorResponse(GroupErrorCode.USER_IS_JOINED_GROUP);

    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> removeGroupMember(String groupId, Integer appId, String memberId) {
        ResponseVO<ImUserDataEntity> singleUserInfo = imUserService.getSingleUserInfo(memberId, appId);
        if (!singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(singleUserInfo.getCode(), singleUserInfo.getMsg());
        }
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);
        if (!roleInGroupOne.isOk()) {
            return ResponseVO.errorResponse(roleInGroupOne.getCode(), roleInGroupOne.getMsg());
        }
        GetRoleInGroupResp data = roleInGroupOne.getData();
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setLeaveTime(System.currentTimeMillis());
        imGroupMemberEntity.setGroupMemberId(data.getGroupMemberId());
        imGroupMemberMapper.updateById(imGroupMemberEntity);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String memberId, Integer appId) {
        GetRoleInGroupResp resp = new GetRoleInGroupResp();
        QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
        queryOwner.eq("group_id", groupId);
        queryOwner.eq("app_id", appId);
        queryOwner.eq("member_id", memberId);
        ImGroupMemberEntity imGroupMemberEntity = imGroupMemberMapper.selectOne(queryOwner);
        if (imGroupMemberEntity == null || imGroupMemberEntity.getRole().equals(GroupMemberRoleEnum.LEAVE.getCode())) {
            return ResponseVO.errorResponse(GroupErrorCode.MEMBER_IS_NOT_JOINED_GROUP);
        }
        resp.setSpeakDate(imGroupMemberEntity.getSpeakDate());
        resp.setGroupMemberId(imGroupMemberEntity.getGroupMemberId());
        resp.setMemberId(imGroupMemberEntity.getMemberId());
        resp.setRole(imGroupMemberEntity.getRole());
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<List<String>> getMemberJoinedGroup(GetJoinedGroupReq req) {
        if (req.getLimit() != null) {
            Page<ImGroupMemberEntity> objectPage = new Page<>(req.getOffset(), req.getLimit());
            QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.eq("member_id", req.getMemberId());
            IPage<ImGroupMemberEntity> imGroupMemberEntityPage = imGroupMemberMapper.selectPage(objectPage, query);

            Set<String> groupId = new HashSet<>();
            List<ImGroupMemberEntity> records = imGroupMemberEntityPage.getRecords();
            records.forEach(e -> {
                groupId.add(e.getGroupId());
            });
            return ResponseVO.successResponse(List.copyOf(groupId));
        } else {
            return ResponseVO.successResponse(imGroupMemberMapper.getJoinedGroupId(req.getAppId(), req.getMemberId()));
        }
    }

    @Override
    public ResponseVO<List<AddMemberResp>> addMember(AddGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();
        boolean isAdmin = false;
        ResponseVO<ImGroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(groupResp.getCode(), groupResp.getMsg());
        }
        List<GroupMemberDto> memberDtos = req.getMembers();

        // 事件之前回调
        if (appConfig.isAddGroupMemberBeforeCallback()) {
            ResponseVO responseVO = callbackService.beforeCallback(req.getAppId(),
                    CallbackCommand.GROUP_MEMBER_ADD_BEFORE
                    , JSONObject.toJSONString(req));
            if (!responseVO.isOk()) {
                return responseVO;
            }
            try {
                // 成员信息回调，用户可选择是否变更添加人员
                memberDtos = JSONArray.parseArray(
                        JSONObject.toJSONString(responseVO.getData()),
                        GroupMemberDto.class);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("GroupMemberAddBefore 回调失败：{}", req.getAppId());
            }
        }

        ImGroupEntity group = (ImGroupEntity) groupResp.getData();
        /*
         * 私有群（private）	类似普通微信群，创建后仅支持已在群内的好友邀请加群，且无需被邀请方同意或群主审批
         * 公开群（Public）	类似 QQ 群，创建后群主可以指定群管理员，需要群主或管理员审批通过才能入群
         * 群类型 1私有群（类似微信） 2公开群(类似qq）
         */
        if (!isAdmin && GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
            throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
        }

        List<String> successId = new ArrayList<>();
        for (GroupMemberDto memberId :
                memberDtos) {
            ResponseVO<ResponseVO.NoDataReturn> responseVO = null;
            try {
                responseVO = groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                e.printStackTrace();
                responseVO = ResponseVO.errorResponse();
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(memberId.getMemberId());
            if (responseVO.isOk()) {
                successId.add(memberId.getMemberId());
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCode.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
                addMemberResp.setResultMessage(responseVO.getMsg());
            } else {
                addMemberResp.setResult(1);
                addMemberResp.setResultMessage(responseVO.getMsg());
            }
            resp.add(addMemberResp);
        }
        // TCP 通知
        AddGroupMemberPack addGroupMemberPack = new AddGroupMemberPack();
        addGroupMemberPack.setGroupId(req.getGroupId());
        addGroupMemberPack.setMembers(successId);
        groupMessageProducer.producer(req.getOperator(), GroupEventCommand.ADDED_MEMBER, addGroupMemberPack
                , new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        //回调
        if (appConfig.isAddGroupMemberAfterCallback()) {
            AddMemberAfterCallback dto = new AddMemberAfterCallback();
            dto.setGroupId(req.getGroupId());
            dto.setGroupType(group.getGroupType());
            dto.setMemberId(resp);
            dto.setOperator(req.getOperator());
            callbackService.afterCallback(req.getAppId()
                    , CallbackCommand.GROUP_MEMBER_ADD_AFTER,
                    JSONObject.toJSONString(dto));
        }
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> removeMember(RemoveGroupMemberReq req) {

        boolean isAdmin = false;
        ResponseVO<ImGroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(groupResp.getCode(), groupResp.getMsg());
        }
        ImGroupEntity group = (ImGroupEntity) groupResp.getData();
        if (!isAdmin) {
            if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
                //获取操作人的权限 是管理员 or 群主 or 群成员
                ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!role.isOk()) {
                    return ResponseVO.errorResponse(role.getCode(), role.getMsg());
                }
                GetRoleInGroupResp data = (GetRoleInGroupResp) role.getData();
                Integer roleInfo = data.getRole();
                boolean isOwner = roleInfo.equals(GroupMemberRoleEnum.OWNER.getCode());
                boolean isManager = roleInfo.equals(GroupMemberRoleEnum.MANAGER.getCode());
                // 既不是群主也不是管理员
                if (!isOwner && !isManager) {
                    throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                }
                //私有群必须是群主才能踢人
                if (!isOwner && GroupTypeEnum.PRIVATE.getCode() == group.getGroupType()) {
                    throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }
                // 公开群管理员和群主可踢人，但管理员只能踢普通群成员
                if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
//                    throw new ApplicationException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                    // 获取被踢人的权限
                    ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                    if (!roleInGroupOne.isOk()) {
                        return ResponseVO.errorResponse(roleInGroupOne.getCode(), roleInGroupOne.getMsg());
                    }
                    GetRoleInGroupResp memberRole = roleInGroupOne.getData();
                    if (memberRole.getRole().equals(GroupMemberRoleEnum.OWNER.getCode())) {
                        throw new YoungImException(GroupErrorCode.GROUP_OWNER_IS_NOT_REMOVE);
                    }
                    // 是管理员并且被踢人不是群成员，无法操作
                    if (isManager && !memberRole.getRole().equals(GroupMemberRoleEnum.ORDINARY.getCode())) {
                        throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                    }
                }
            }
        }
        ResponseVO<ResponseVO.NoDataReturn> responseVO = groupMemberService.removeGroupMember(req.getGroupId(), req.getAppId(), req.getMemberId());
        // 事件之后回调
        if (responseVO.isOk()) {
            if (appConfig.isDeleteGroupMemberAfterCallback()) {
                callbackService.afterCallback(req.getAppId(),
                        CallbackCommand.GROUP_MEMBER_DELETE_AFTER,
                        JSONObject.toJSONString(req));
            }
        }
        return responseVO;
    }

    @Override
    public ResponseVO<List<GroupMemberDto>> getGroupMember(String groupId, Integer appId) {
        List<GroupMemberDto> groupMember = imGroupMemberMapper.getGroupMember(appId, groupId);
        return ResponseVO.successResponse(groupMember);
    }

    @Override
    public List<String> getGroupMemberId(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupMemberId(appId, groupId);
    }

    @Override
    public List<GroupMemberDto> getGroupManager(String groupId, Integer appId) {
        return imGroupMemberMapper.getGroupManager(groupId, appId);
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> updateGroupMember(UpdateGroupMemberReq req) {
        boolean isadmin = false;
        // 获取群基本信息
        ResponseVO<ImGroupEntity> group = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(group.getCode(), group.getMsg());
        }
        ImGroupEntity groupData = group.getData();
        if (groupData.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new YoungImException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        // 是否是自己修改自己的资料
        boolean isMeOperate = req.getOperator().equals(req.getMemberId());
        if (!isadmin) {
            // 昵称只能自己修改 权限只能群主或管理员修改
            if (StringUtils.isBlank(req.getAlias()) && !isMeOperate) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_ONESELF);
            }
            // 如果要修改权限相关的则走下面的逻辑
            if (req.getRole() != null) {
                // 私有群不能设置管理员
                if (groupData.getGroupType() == GroupTypeEnum.PRIVATE.getCode() &&
                        req.getRole() != null && (req.getRole().equals(GroupMemberRoleEnum.MANAGER.getCode()) ||
                        req.getRole().equals(GroupMemberRoleEnum.OWNER.getCode()))) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
                }
                // 获取被操作人的是否在群内
                ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                if (!roleInGroupOne.isOk()) {
                    return ResponseVO.errorResponse(roleInGroupOne.getCode(), roleInGroupOne.getMsg());
                }
                // 获取操作人权限
                ResponseVO<GetRoleInGroupResp> operateRoleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!operateRoleInGroupOne.isOk()) {
                    return ResponseVO.errorResponse(operateRoleInGroupOne.getCode(), operateRoleInGroupOne.getMsg());
                }
                GetRoleInGroupResp data = operateRoleInGroupOne.getData();
                Integer roleInfo = data.getRole();
                boolean isOwner = roleInfo.equals(GroupMemberRoleEnum.OWNER.getCode());
                boolean isManager = roleInfo.equals(GroupMemberRoleEnum.MANAGER.getCode());
                // 不是管理员不能修改权限
                if (req.getRole() != null && !isOwner && !isManager) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
                }
                // 管理员只有群主能够设置
                if (req.getRole() != null && req.getRole().equals(GroupMemberRoleEnum.MANAGER.getCode()) && !isOwner) {
                    return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
                }
            }
        }
        ImGroupMemberEntity update = new ImGroupMemberEntity();

        if (StringUtils.isNotBlank(req.getAlias())) {
            update.setAlias(req.getAlias());
        }
        //不能直接修改为群主
        if (req.getRole() != null && !req.getRole().equals(GroupMemberRoleEnum.OWNER.getCode())) {
            update.setRole(req.getRole());
        }
        UpdateWrapper<ImGroupMemberEntity> objectUpdateWrapper = new UpdateWrapper<>();
        objectUpdateWrapper.eq("app_id", req.getAppId());
        objectUpdateWrapper.eq("member_id", req.getMemberId());
        objectUpdateWrapper.eq("group_id", req.getGroupId());
        imGroupMemberMapper.update(update, objectUpdateWrapper);
        UpdateGroupMemberPack pack = new UpdateGroupMemberPack();
        pack.setGroupId(req.getGroupId());
        pack.setMemberId(req.getMemberId());
        pack.setAlias(req.getAlias());
        pack.setExtra(req.getExtra());

        groupMessageProducer.producer(req.getOperator(), GroupEventCommand.UPDATED_MEMBER, pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> transferGroupMember(String owner, String groupId, Integer appId) {
        //更新旧群主
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        UpdateWrapper<ImGroupMemberEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("app_id", appId);
        updateWrapper.eq("group_id", groupId);
        updateWrapper.eq("role", GroupMemberRoleEnum.OWNER.getCode());
        imGroupMemberMapper.update(imGroupMemberEntity, updateWrapper);

        //更新新群主
        ImGroupMemberEntity newOwner = new ImGroupMemberEntity();
        newOwner.setRole(GroupMemberRoleEnum.OWNER.getCode());
        UpdateWrapper<ImGroupMemberEntity> ownerWrapper = new UpdateWrapper<>();
        ownerWrapper.eq("app_id", appId);
        ownerWrapper.eq("group_id", groupId);
        ownerWrapper.eq("member_id", owner);
        imGroupMemberMapper.update(newOwner, ownerWrapper);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> speak(SpeaMemberReq req) {

        ResponseVO<ImGroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(groupResp.getCode(), groupResp.getMsg());
        }
        boolean isadmin = false;
        boolean isOwner = false;
        boolean isManager = false;
        GetRoleInGroupResp memberRole = null;
        if (!isadmin) {
            //获取操作人的权限 是管理员or群主or群成员
            ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!role.isOk()) {
                return ResponseVO.errorResponse(role.getCode(), role.getMsg());
            }

            GetRoleInGroupResp data = (GetRoleInGroupResp) role.getData();
            Integer roleInfo = data.getRole();

            isOwner = roleInfo.equals(GroupMemberRoleEnum.OWNER.getCode());
            isManager = roleInfo.equals(GroupMemberRoleEnum.MANAGER.getCode());

            if (!isOwner && !isManager) {
                throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return ResponseVO.errorResponse(roleInGroupOne.getCode(), roleInGroupOne.getMsg());
            }
            memberRole = roleInGroupOne.getData();
            //被操作人是群主只能app管理员操作
            if (memberRole.getRole().equals(GroupMemberRoleEnum.OWNER.getCode())) {
                throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_APPMANAGER_ROLE);
            }
            //是管理员并且被操作人不是群成员，无法操作
            if (isManager && !memberRole.getRole().equals(GroupMemberRoleEnum.ORDINARY.getCode())) {
                throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        if (memberRole == null) {
            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return ResponseVO.errorResponse(roleInGroupOne.getCode(), roleInGroupOne.getMsg());
            }
            memberRole = roleInGroupOne.getData();
        }
        imGroupMemberEntity.setGroupMemberId(memberRole.getGroupMemberId());
        if (req.getSpeakDate() > 0) {
            imGroupMemberEntity.setSpeakDate(System.currentTimeMillis() + req.getSpeakDate());
        } else {
            // 解除禁言
            imGroupMemberEntity.setSpeakDate(req.getSpeakDate());
        }
        int i = imGroupMemberMapper.updateById(imGroupMemberEntity);
        if (i == 1) {
            GroupMemberSpeakPack pack = new GroupMemberSpeakPack();
            pack.setGroupId(req.getGroupId());
            pack.setMemberId(req.getMemberId());
            pack.setSpeakDate(req.getSpeakDate());

            groupMessageProducer.producer(req.getOperator(), GroupEventCommand.SPEAK_GROUP_MEMBER, pack,
                    new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        }
        return ResponseVO.successResponse();
    }
}
