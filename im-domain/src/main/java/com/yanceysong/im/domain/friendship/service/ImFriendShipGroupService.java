package com.yanceysong.im.domain.friendship.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipGroupEntity;
import com.yanceysong.im.domain.friendship.model.req.AddFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.model.req.DeleteFriendShipGroupReq;

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

    ResponseVO getGroup(String fromId, String groupName, Integer appId);

}