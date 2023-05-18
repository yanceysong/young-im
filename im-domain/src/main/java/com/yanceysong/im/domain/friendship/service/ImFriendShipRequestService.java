package com.yanceysong.im.domain.friendship.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.friendship.model.req.friend.ApproverFriendRequestReq;
import com.yanceysong.im.domain.friendship.model.req.friend.FriendDto;
import com.yanceysong.im.domain.friendship.model.req.friend.ReadFriendShipRequestReq;

/**
 * @ClassName ImFriendShipRequestService
 * @Description
 * @date 2023/5/5 11:03
 * @Author yanceysong
 * @Version 1.0
 */
public interface ImFriendShipRequestService {

    /**
     * 好友申请
     * @param fromId
     * @param dto
     * @param appId
     * @return
     */
     ResponseVO<ResponseVO.NoDataReturn> addFienshipRequest(String fromId, FriendDto dto, Integer appId);

    /**
     * 好友审批
     * @param req
     * @return
     */
    ResponseVO approverFriendRequest(ApproverFriendRequestReq req);

    /**
     * 好友申请读取情况【已读、未读】
     * @param req
     * @return
     */
    ResponseVO<ResponseVO.NoDataReturn> readFriendShipRequestReq(ReadFriendShipRequestReq req);

    /**
     * 获取好友申请
     * @param fromId
     * @param appId
     * @return
     */
    ResponseVO getFriendRequest(String fromId, Integer appId);
}
