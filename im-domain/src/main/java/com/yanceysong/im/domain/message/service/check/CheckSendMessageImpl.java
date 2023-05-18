package com.yanceysong.im.domain.message.service.check;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.error.MessageErrorCode;
import com.yanceysong.im.common.enums.friend.FriendShipErrorCode;
import com.yanceysong.im.common.enums.friend.FriendShipStatusEnum;
import com.yanceysong.im.common.enums.group.GroupErrorCode;
import com.yanceysong.im.common.enums.group.GroupMemberRoleEnum;
import com.yanceysong.im.common.enums.group.GroupMuteTypeEnum;
import com.yanceysong.im.common.enums.user.UserForbiddenFlagEnum;
import com.yanceysong.im.common.enums.user.UserSilentFlagEnum;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipEntity;
import com.yanceysong.im.domain.friendship.model.req.friend.GetRelationReq;
import com.yanceysong.im.domain.friendship.service.ImFriendService;
import com.yanceysong.im.domain.group.dao.ImGroupEntity;
import com.yanceysong.im.domain.group.model.resp.GetRoleInGroupResp;
import com.yanceysong.im.domain.group.service.ImGroupMemberService;
import com.yanceysong.im.domain.group.service.ImGroupService;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.config.AppConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName CheckSendMessageService
 * @Description
 * @date 2023/5/16 10:42
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class CheckSendMessageImpl implements CheckSendMessage {

    @Resource
    private ImUserService userService;

    @Resource
    private ImFriendService friendService;

    @Resource
    private ImGroupService groupService;

    @Resource
    private ImGroupMemberService groupMemberService;

    @Resource
    private AppConfig appConfig;

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> checkSenderForbidAndMute(String fromId, Integer appId) {
        // 查询用户是否存在
        ResponseVO<ImUserDataEntity> singleUserInfo = userService.getSingleUserInfo(fromId, appId);
        if (!singleUserInfo.isOk()) {
            return ResponseVO.errorResponse(singleUserInfo.getCode(), singleUserInfo.getMsg());
        }

        // 用户是否被禁言或禁用
        if (UserForbiddenFlagEnum.FORBIBBEN.getCode() == singleUserInfo.getData().getForbiddenFlag()) {
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_FORBIBBEN);
        } else if (UserSilentFlagEnum.MUTE.getCode() == singleUserInfo.getData().getSilentFlag()) {
            return ResponseVO.errorResponse(MessageErrorCode.FROMER_IS_MUTE);
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> checkFriendShip(String fromId, String toId, Integer appId) {

        if (appConfig.isSendMessageCheckFriend()) {
            // 自己与对方的好友关系链是否正常【库表是否有这行记录: from2to】
            ResponseVO<ImFriendShipEntity> fromRelation = getRelation(fromId, toId, appId);
            if (!fromRelation.isOk()) {
                return ResponseVO.errorResponse(fromRelation.getCode(), fromRelation.getMsg());
            }
            ImFriendShipEntity fromRelationEntity = fromRelation.getData();
            // 对方与自己的好友关系链是否正常【库表是否有这行记录: to2from】
            ResponseVO<ImFriendShipEntity> toRelation = getRelation(toId, fromId, appId);
            if (!toRelation.isOk()) {
                return ResponseVO.errorResponse(toRelation.getCode(), toRelation.getMsg());
            }
            ImFriendShipEntity toRelationEntity1 = toRelation.getData();
            // 检查自己是否删除对方【status = 2（删除）】
            if (FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()
                    .equals(fromRelationEntity.getStatus())) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED);
            }

            // 检查对方是否删除己方【status = 2（删除）】
            if (FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()
                    .equals(toRelationEntity1.getStatus())) {
                return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_DELETED_YOU);
            }

            // 检查黑名单列表中
            if (appConfig.isSendMessageCheckBlack()) {
                // 检查自己是否拉黑对方
                if (FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()
                        .equals(fromRelationEntity.getBlack())) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.FRIEND_IS_BLACK);
                }
                // 检查对方是否拉黑自己
                if (FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()
                        .equals(toRelationEntity1.getBlack())) {
                    return ResponseVO.errorResponse(FriendShipErrorCode.TARGET_IS_BLACK_YOU);
                }
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ResponseVO.NoDataReturn> checkGroupMessage(String fromId, String groupId, Integer appId) {
        // 校验发送方是否被禁言或封禁
        ResponseVO<ResponseVO.NoDataReturn> responseVO = checkSenderForbidAndMute(fromId, appId);
        if (!responseVO.isOk()) {
            return ResponseVO.errorResponse(responseVO.getCode(), responseVO.getMsg());
        }
        // 数据库查询是否有该群
        ResponseVO<ImGroupEntity> group = groupService.getGroup(groupId, appId);
        if (!group.isOk()) {
            return ResponseVO.errorResponse(group.getCode(), group.getMsg());
        }
        // 查询该成员是否在群，在群里为什么角色
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = groupMemberService.getRoleInGroupOne(groupId, fromId, appId);
        if (!roleInGroupOne.isOk()) {
            return ResponseVO.errorResponse(roleInGroupOne.getCode(), roleInGroupOne.getMsg());
        }
        GetRoleInGroupResp data = roleInGroupOne.getData();
        // 查询群内是否禁言
        // 如果禁言，只有群主和管理员才有权说话
        ImGroupEntity groupData = group.getData();
        boolean isGroupMute = GroupMuteTypeEnum.MUTE.getCode().equals(groupData.getMute());
        boolean isManager = GroupMemberRoleEnum.MANAGER.getCode().equals(data.getRole());
        boolean isOwner = GroupMemberRoleEnum.OWNER.getCode().equals(data.getRole());
        if (isGroupMute) {
            if (!(isManager || isOwner)) {
                return ResponseVO.errorResponse(GroupErrorCode.THIS_GROUP_IS_MUTE);
            }
        }
        // 禁言过期时间大于当前时间
        if (data.getSpeakDate() != null && data.getSpeakDate() > System.currentTimeMillis()) {
            return ResponseVO.errorResponse(GroupErrorCode.GROUP_MEMBER_IS_SPEAK);
        }
        return ResponseVO.successResponse();
    }

    private ResponseVO<ImFriendShipEntity> getRelation(String fromId, String toId, Integer appId) {
        GetRelationReq getRelationReq = new GetRelationReq();
        getRelationReq.setFromId(fromId);
        getRelationReq.setToId(toId);
        getRelationReq.setAppId(appId);
        return friendService.getRelation(getRelationReq);
    }
}
