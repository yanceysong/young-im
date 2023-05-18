package com.yanceysong.im.domain.friendship.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupEntity;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.model.req.friend.DeleteFriendShipGroupReq;

/**
 * @ClassName ImFriendShipGroupService
 * @Description
 * @date 2023/5/5 11:03
 * @Author yanceysong
 * @Version 1.0
 */
public interface ImFriendShipGroupService {

    ResponseVO addGroup(AddFriendShipGroupReq req);

    ResponseVO deleteGroup(DeleteFriendShipGroupReq req);

    ResponseVO<ImFriendShipGroupEntity> getGroup(String fromId, String groupName, Integer appId);

}