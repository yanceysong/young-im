package com.yanceysong.im.domain.friendship.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.friendship.model.req.ApproverFriendRequestReq;
import com.yanceysong.im.domain.friendship.model.req.GetFriendShipRequestReq;
import com.yanceysong.im.domain.friendship.model.req.ReadFriendShipRequestReq;
import com.yanceysong.im.domain.friendship.service.ImFriendShipRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @ClassName ImFriendShipRequestController
 * @Description
 * @date 2023/5/5 10:43
 * @Author yanceysong
 * @Version 1.0
 */
public class ImFriendShipRequestController {
    @Autowired
    ImFriendShipRequestService imFriendShipRequestService;

    @RequestMapping("/approveFriendRequest")
    public ResponseVO approveFriendRequest(@RequestBody @Validated
                                           ApproverFriendRequestReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return imFriendShipRequestService.approverFriendRequest(req);
    }

    @RequestMapping("/getFriendRequest")
    public ResponseVO getFriendRequest(@RequestBody @Validated GetFriendShipRequestReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipRequestService.getFriendRequest(req.getFromId(), req.getAppId());
    }

    @RequestMapping("/readFriendShipRequestReq")
    public ResponseVO readFriendShipRequestReq(@RequestBody @Validated ReadFriendShipRequestReq req, Integer appId) {
        req.setAppId(appId);
        return imFriendShipRequestService.readFriendShipRequestReq(req);
    }

}
