package com.yanceysong.im.domain.group.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.yanceysong.im.codec.pack.group.CreateGroupPack;
import com.yanceysong.im.codec.pack.group.DestroyGroupPack;
import com.yanceysong.im.codec.pack.group.UpdateGroupInfoPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.CallbackCommand;
import com.yanceysong.im.common.constant.SeqConstants;
import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.enums.group.GroupErrorCode;
import com.yanceysong.im.common.enums.group.GroupMemberRoleEnum;
import com.yanceysong.im.common.enums.group.GroupStatusEnum;
import com.yanceysong.im.common.enums.group.GroupTypeEnum;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.common.model.ClientInfo;
import com.yanceysong.im.common.model.SyncReq;
import com.yanceysong.im.common.model.SyncResp;
import com.yanceysong.im.domain.group.GroupMessageProducer;
import com.yanceysong.im.domain.group.dao.ImGroupEntity;
import com.yanceysong.im.domain.group.dao.mapper.ImGroupMapper;
import com.yanceysong.im.domain.group.model.req.callback.DestroyGroupCallbackDto;
import com.yanceysong.im.domain.group.model.req.group.*;
import com.yanceysong.im.domain.group.model.resp.GetGroupResp;
import com.yanceysong.im.domain.group.model.resp.GetJoinedGroupResp;
import com.yanceysong.im.domain.group.model.resp.GetRoleInGroupResp;
import com.yanceysong.im.domain.group.service.ImGroupMemberService;
import com.yanceysong.im.domain.group.service.ImGroupService;
import com.yanceysong.im.domain.message.seq.RedisSequence;
import com.yanceysong.im.infrastructure.callback.CallbackService;
import com.yanceysong.im.infrastructure.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @ClassName ImGroupServiceImpl
 * @Description
 * @date 2023/5/5 11:51
 * @Author yanceysong
 * @Version 1.0
 */
@Service
@Slf4j
public class ImGroupServiceImpl implements ImGroupService {

    @Resource
    private ImGroupMapper imGroupDataMapper;

    @Resource
    private ImGroupMemberService imGroupMemberService;
    @Resource
    private CallbackService callbackService;

    @Resource
    private AppConfig appConfig;
    @Resource
    private GroupMessageProducer groupMessageProducer;
    @Resource
    private RedisSequence redisSequence;

    /**
     * 导入群组
     *
     * @param req 群组的请求
     * @return 结果
     */
    @Override
    public ResponseVO<ResponseVO.NoDataReturn> importGroup(ImportGroupReq req) {
        //导入之前判断这个群组是否存在
        if (StringUtils.isEmpty(req.getGroupId())) {
            // 如果请求没有群组 id，使用 UUID 策略自动生成一个唯一群组 ID
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
            query.eq("group_id", req.getGroupId());
            query.eq("app_id", req.getAppId());
            Long groupCount = imGroupDataMapper.selectCount(query);
            if (groupCount > 0) {
                log.info("插入群组失败,该群组已经存在,无需导入,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
                throw new YoungImException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        //群组不存在才进行导入
        ImGroupEntity imGroupEntity = new ImGroupEntity();
        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            log.info("插入群组失败,导入的群必须有群主,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        if (req.getCreateTime() == null) {
            imGroupEntity.setCreateTime(System.currentTimeMillis());
        }
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        imGroupEntity.setGroupId(req.getGroupId());
        imGroupEntity.setAppId(req.getAppId());
        imGroupEntity.setOwnerId(req.getOwnerId());
        imGroupEntity.setGroupType(req.getGroupType());
        imGroupEntity.setGroupName(req.getGroupName());
        imGroupEntity.setMute(req.getMute());
        imGroupEntity.setApplyJoinType(req.getApplyJoinType());
        imGroupEntity.setIntroduction(req.getIntroduction());
        imGroupEntity.setNotification(req.getNotification());
        imGroupEntity.setPhoto(req.getPhoto());
        imGroupEntity.setMaxMemberCount(req.getMaxMemberCount());
        imGroupEntity.setExtra(req.getExtra());
        int insertResult = imGroupDataMapper.insert(imGroupEntity);

        if (insertResult != 1) {
            log.error("插入群组失败,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.IMPORT_GROUP_ERROR);
        }
        log.info("插入群组成功,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
        return ResponseVO.successResponse();
    }

    /**
     * 创建一个群组
     *
     * @param req 创建群组请求
     * @return 创建群组结果
     */
    @Override
    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> createGroup(CreateGroupReq req) {
        boolean isAdmin = false;
        if (!isAdmin) {
            req.setOwnerId(req.getOperator());
        }
        // 1.判断群 id 是否存在
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        //群组id如果为空那么就是uuid
        if (StringUtils.isEmpty(req.getGroupId())) {
            req.setGroupId(UUID.randomUUID().toString().replace("-", ""));
        } else {
            query.eq("group_id", req.getGroupId());
            query.eq("app_id", req.getAppId());
            Long integerResult = imGroupDataMapper.selectCount(query);
            if (integerResult > 0) {
                log.info("创建群组失败,该群组已经存在,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
                throw new YoungImException(GroupErrorCode.GROUP_IS_EXIST);
            }
        }
        // 公开群需要指定群主
        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StringUtils.isBlank(req.getOwnerId())) {
            log.info("创建群组失败,创建群必须有群主,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }
        ImGroupEntity imGroupEntity = new ImGroupEntity();
        long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.GROUP_SEQ);
        imGroupEntity.setSequence(seq);

        imGroupEntity.setCreateTime(System.currentTimeMillis());
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        imGroupEntity.setGroupId(req.getGroupId());
        imGroupEntity.setAppId(req.getAppId());
        imGroupEntity.setOwnerId(req.getOwnerId());
        imGroupEntity.setGroupType(req.getGroupType());
        imGroupEntity.setGroupName(req.getGroupName());
        imGroupEntity.setMute(req.getMute());
        imGroupEntity.setApplyJoinType(req.getApplyJoinType());
        imGroupEntity.setIntroduction(req.getIntroduction());
        imGroupEntity.setNotification(req.getNotification());
        imGroupEntity.setPhoto(req.getPhoto());
        imGroupEntity.setMaxMemberCount(req.getMaxMemberCount());
        imGroupEntity.setExtra(req.getExtra());
        int groupInsertResult = imGroupDataMapper.insert(imGroupEntity);
        if (groupInsertResult == 0) {
            log.info("创建群组失败群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.CREATE_GROUP_ERROR);
        }
        // 群主插入 GroupMember 表，并将其设置成 owner 权限
        GroupMemberDto groupMemberDto = new GroupMemberDto();
        groupMemberDto.setMemberId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRoleEnum.OWNER.getCode());
        groupMemberDto.setJoinTime(System.currentTimeMillis());
        imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), groupMemberDto);
        //插入群成员
        for (GroupMemberDto dto : req.getMember()) {
            imGroupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), dto);
        }
        // 发送 TCP 通知
        CreateGroupPack createGroupPack = new CreateGroupPack();
        createGroupPack.setGroupId(imGroupEntity.getGroupId());
        createGroupPack.setAppId(imGroupEntity.getAppId());
        createGroupPack.setOwnerId(imGroupEntity.getOwnerId());
        createGroupPack.setGroupType(imGroupEntity.getGroupType());
        createGroupPack.setGroupName(imGroupEntity.getGroupName());
        createGroupPack.setMute(imGroupEntity.getMute());
        createGroupPack.setApplyJoinType(imGroupEntity.getApplyJoinType());
        createGroupPack.setIntroduction(imGroupEntity.getIntroduction());
        createGroupPack.setNotification(imGroupEntity.getNotification());
        createGroupPack.setPhoto(imGroupEntity.getPhoto());
        createGroupPack.setStatus(imGroupEntity.getStatus());
        createGroupPack.setSequence(imGroupEntity.getSequence());
        createGroupPack.setCreateTime(imGroupEntity.getCreateTime());
        createGroupPack.setExtra(imGroupEntity.getExtra());

        groupMessageProducer.producer(req.getOperator(), GroupEventCommand.CREATED_GROUP, createGroupPack,
                new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        // 之后回调
        if (appConfig.isCreateGroupAfterCallback()) {
            callbackService.afterCallback(req.getAppId(),
                    CallbackCommand.CREATE_GROUP_AFTER,
                    JSONObject.toJSONString(imGroupEntity));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 更新群组信息
     *
     * @param req 更新群组信息请求
     * @return 更新结果
     */
    @Override
    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> updateBaseGroupInfo(UpdateGroupReq req) {
        //1.判断群id是否存在
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq("group_id", req.getGroupId());
        query.eq("app_id", req.getAppId());
        ImGroupEntity imGroupEntity = imGroupDataMapper.selectOne(query);
        if (imGroupEntity == null) {
            log.info("修改群组信息失败,该群组不存在,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        if (imGroupEntity.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            log.info("修改群组信息失败,该群组已经解散,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        boolean isAdmin = false;
        if (!isAdmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!role.isOk()) {
                // 用户不在群内
                return ResponseVO.errorResponse(role.getCode(), role.getMsg());
            }
            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();
            boolean isManager = roleInfo.equals(GroupMemberRoleEnum.MANAGER.getCode()) || roleInfo.equals(GroupMemberRoleEnum.OWNER.getCode());
            //公开群只能群主修改资料
            if (!isManager && GroupTypeEnum.PUBLIC.getCode() == imGroupEntity.getGroupType()) {
                log.info("修改群组信息失败,该群只能群主和管理员修改,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
                throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }
        long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.GROUP_SEQ);

        ImGroupEntity update = new ImGroupEntity();
        update.setSequence(seq);
        update.setGroupId(req.getGroupId());
        update.setAppId(req.getAppId());
        update.setGroupName(req.getGroupName());
        update.setMute(req.getMute());
        update.setApplyJoinType(req.getApplyJoinType());
        update.setIntroduction(req.getIntroduction());
        update.setNotification(req.getNotification());
        update.setPhoto(req.getPhoto());
        update.setMaxMemberCount(req.getMaxMemberCount());
        update.setExtra(req.getExtra());

        update.setUpdateTime(System.currentTimeMillis());
        int row = imGroupDataMapper.update(update, query);
        if (row != 1) {
            throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
        }

        // 发送 TCP 通知
        UpdateGroupInfoPack pack = new UpdateGroupInfoPack();
        pack.setSequence(seq);
        pack.setGroupId(req.getGroupId());
        pack.setGroupName(req.getGroupName());
        pack.setMute(req.getMute());
        pack.setIntroduction(req.getIntroduction());
        pack.setNotification(req.getNotification());
        pack.setPhoto(req.getPhoto());

        groupMessageProducer.producer(req.getOperator(), GroupEventCommand.UPDATED_GROUP,
                pack, new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        // 之后回调
        if (appConfig.isModifyGroupAfterCallback()) {
            callbackService.afterCallback(req.getAppId(),
                    CallbackCommand.UPDATE_GROUP_AFTER,
                    // 将修改之后的群聊信息查询给服务器 TCP 服务层
                    JSONObject.toJSONString(imGroupDataMapper.selectOne(query)));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 获取某个用户已经加入的群组
     *
     * @param req 请求 包含用户的id和要拉取的群组类型
     * @return 请求结果
     */
    @Override
    public ResponseVO<GetJoinedGroupResp> getJoinedGroup(GetJoinedGroupReq req) {
        // 1. 获取用户加入所有群 ID
        ResponseVO<List<String>> memberJoinedGroup = imGroupMemberService.getMemberJoinedGroup(req);
        if (memberJoinedGroup.isOk()) {
            GetJoinedGroupResp resp = new GetJoinedGroupResp();
            if (CollectionUtils.isEmpty(memberJoinedGroup.getData())) {
                resp.setTotalCount(0L);
                resp.setGroupList(new ArrayList<>());
                return ResponseVO.successResponse(resp);
            }
            //拿到这个用户的id参加的群组id，然后根据群组id去查所有的群信息
            QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.in("group_id", memberJoinedGroup.getData());
            if (CollectionUtils.isNotEmpty(req.getGroupType())) {
                query.in("group_type", req.getGroupType());
            }
            List<ImGroupEntity> groupList = imGroupDataMapper.selectList(query);
            resp.setGroupList(groupList);
            if (req.getLimit() == null) {
                resp.setTotalCount((long) groupList.size());
            } else {
                resp.setTotalCount(imGroupDataMapper.selectCount(query));
            }
            return ResponseVO.successResponse(resp);
        } else {
            return ResponseVO.errorResponse(memberJoinedGroup.getCode(), memberJoinedGroup.getMsg());
        }
    }

    /**
     * 解散群组
     *
     * @param req 解散群组请求
     * @return 解散结果
     */
    @Override
    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> destroyGroup(DestroyGroupReq req) {
        boolean isAdmin = false;
        QueryWrapper<ImGroupEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("group_id", req.getGroupId());
        objectQueryWrapper.eq("app_id", req.getAppId());
        ImGroupEntity imGroupEntity = imGroupDataMapper.selectOne(objectQueryWrapper);
        if (imGroupEntity == null) {
            log.info("解散群组失败,群组不存在,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        if (imGroupEntity.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            log.info("解散群组失败,该群已经解散,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        if (!isAdmin) {
            if (imGroupEntity.getGroupType() == GroupTypeEnum.PRIVATE.getCode()) {
                log.info("解散群组失败,私有群不能解散,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
                throw new YoungImException(GroupErrorCode.PRIVATE_GROUP_CAN_NOT_DESTORY);
            }
            if (!imGroupEntity.getOwnerId().equals(req.getOperator())) {
                log.info("解散群组失败,只能群主解散,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
                throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }
        long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.GROUP_SEQ);

        ImGroupEntity update = new ImGroupEntity();
        update.setSequence(seq);
        update.setStatus(GroupStatusEnum.DESTROY.getCode());
        int update1 = imGroupDataMapper.update(update, objectQueryWrapper);
        if (update1 != 1) {
            log.info("解散群组失败,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.UPDATE_GROUP_BASE_INFO_ERROR);
        }
        // 发送 TCP 通知
        DestroyGroupPack pack = new DestroyGroupPack();
        pack.setSequence(seq);
        pack.setGroupId(req.getGroupId());
        groupMessageProducer.producer(req.getOperator(),
                GroupEventCommand.DESTROY_GROUP, pack,
                new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));


        if (appConfig.isModifyGroupAfterCallback()) {
            DestroyGroupCallbackDto dto = new DestroyGroupCallbackDto();
            dto.setGroupId(req.getGroupId());
            callbackService.afterCallback(req.getAppId()
                    , CallbackCommand.DESTROY_GROUP_AFTER,
                    JSONObject.toJSONString(dto));
        }
        return ResponseVO.successResponse();
    }

    /**
     * 转让群组
     *
     * @param req 请求
     * @return 转让结果
     */
    @Override
    @Transactional
    public ResponseVO<ResponseVO.NoDataReturn> transferGroup(TransferGroupReq req) {
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return ResponseVO.errorResponse(roleInGroupOne.getCode(), roleInGroupOne.getMsg());
        }
        GetRoleInGroupResp role = roleInGroupOne.getData();
        if (!role.getRole().equals(GroupMemberRoleEnum.OWNER.getCode())) {
            log.info("转让群组失败,只有群主可以转让,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            return ResponseVO.errorResponse(GroupErrorCode.THIS_OPERATE_NEED_OWNER_ROLE);
        }
        ResponseVO<GetRoleInGroupResp> newOwnerRole = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());
        if (!newOwnerRole.isOk()) {
            return ResponseVO.errorResponse(newOwnerRole.getCode(), newOwnerRole.getMsg());
        }
        QueryWrapper<ImGroupEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("group_id", req.getGroupId());
        objectQueryWrapper.eq("app_id", req.getAppId());
        ImGroupEntity imGroupEntity = imGroupDataMapper.selectOne(objectQueryWrapper);
        if (imGroupEntity.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            log.info("转让群组失败,该群已经解散,群组id:{}appId:{}", req.getGroupId(), req.getAppId());
            throw new YoungImException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        long seq = redisSequence.doGetSeq(req.getAppId() + ":" + SeqConstants.GROUP_SEQ);

        ImGroupEntity updateGroup = new ImGroupEntity();
        updateGroup.setOwnerId(req.getOwnerId());
        updateGroup.setSequence(seq);

        UpdateWrapper<ImGroupEntity> updateGroupWrapper = new UpdateWrapper<>();
        updateGroupWrapper.eq("app_id", req.getAppId());
        updateGroupWrapper.eq("group_id", req.getGroupId());
        imGroupDataMapper.update(updateGroup, updateGroupWrapper);
        imGroupMemberService.transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ImGroupEntity> getGroup(String groupId, Integer appId) {
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("group_id", groupId);
        ImGroupEntity imGroupEntity = imGroupDataMapper.selectOne(query);
        if (imGroupEntity == null) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imGroupEntity);
    }

    /**
     * 获取一个群组的信息
     *
     * @param req 获取群组信息请求
     * @return 群组的信息
     */
    @Override
    public ResponseVO<GetGroupResp> getGroup(GetGroupReq req) {
        ResponseVO<ImGroupEntity> group = this.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return ResponseVO.errorResponse(group.getCode(), group.getMsg());
        }
        GetGroupResp getGroupResp = new GetGroupResp();
        BeanUtils.copyProperties(group.getData(), getGroupResp);
        try {
            ResponseVO<List<GroupMemberDto>> groupMember = imGroupMemberService.getGroupMember(req.getGroupId(), req.getAppId());
            if (groupMember.isOk()) {
                List<GroupMemberDto> list = groupMember.getData();
                getGroupResp.setMemberList(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseVO.successResponse(getGroupResp);
    }

    /**
     * 全员禁言
     *
     * @param req 全员禁言参数
     * @return 禁言结果
     */
    @Override
    public ResponseVO<ResponseVO.NoDataReturn> muteGroup(MuteGroupReq req) {
        ResponseVO<ImGroupEntity> groupResp = getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return ResponseVO.errorResponse(groupResp.getCode(), groupResp.getMsg());
        }
        ImGroupEntity group = groupResp.getData();
        if (group.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new YoungImException(GroupErrorCode.GROUP_IS_DESTROY);
        }
        boolean isadmin = false;
        if (!isadmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = imGroupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!role.isOk()) {
                return ResponseVO.errorResponse(role.getCode(), role.getMsg());
            }
            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();
            boolean isManager = roleInfo.equals(GroupMemberRoleEnum.MANAGER.getCode()) || roleInfo.equals(GroupMemberRoleEnum.OWNER.getCode());
            //公开群只能群主修改资料
            if (!isManager) {
                throw new YoungImException(GroupErrorCode.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }
        ImGroupEntity update = new ImGroupEntity();
        update.setMute(req.getMute());
        UpdateWrapper<ImGroupEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq("group_id", req.getGroupId());
        wrapper.eq("app_id", req.getAppId());
        imGroupDataMapper.update(update, wrapper);
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO syncJoinedGroupList(SyncReq req) {
        if (req.getMaxLimit() > appConfig.getJoinGroupMaxCount()) {
            // 前端传输限制，保证一次增量拉取数据量不超过配置文件的值
            req.setMaxLimit(appConfig.getJoinGroupMaxCount());
        }

        SyncResp<ImGroupEntity> resp = new SyncResp<>();

        ResponseVO<Collection<String>> memberJoinedGroup = imGroupMemberService.syncMemberJoinedGroup(req.getOperator(), req.getAppId());
        if (memberJoinedGroup.isOk()) {

            Collection<String> data = memberJoinedGroup.getData();
            QueryWrapper<ImGroupEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("app_id", req.getAppId());
            queryWrapper.in("group_id", data);
            queryWrapper.gt("sequence", req.getLastSequence());
            queryWrapper.last(" limit " + req.getMaxLimit());
            queryWrapper.orderByAsc("sequence");

            List<ImGroupEntity> list = imGroupDataMapper.selectList(queryWrapper);

            if (!CollectionUtils.isEmpty(list)) {
                ImGroupEntity maxSeqEntity = list.get(list.size() - 1);
                resp.setDataList(list);
                //设置最大seq
                Long maxSeq = imGroupDataMapper.getJoinGroupMaxSeq(data, req.getAppId());
                resp.setMaxSequence(maxSeq);
                //设置是否拉取完毕
                resp.setCompleted(maxSeqEntity.getSequence() >= maxSeq);
                return ResponseVO.successResponse(resp);
            }

        }
        resp.setCompleted(true);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public Long getUserGroupMaxSeq(String userId, Integer appId) {
        ResponseVO<Collection<String>> memberJoinedGroup =
                imGroupMemberService.syncMemberJoinedGroup(userId, appId);
        if (!memberJoinedGroup.isOk()) {
            throw new YoungImException(500, "");
        }
        return imGroupDataMapper.getJoinGroupMaxSeq(
                memberJoinedGroup.getData(), appId);
    }
}