package com.yanceysong.im.domain.friendship.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.model.req.friend.DeleteFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.req.friend.DeleteFriendShipGroupReq;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupMemberService;
import com.yanceysong.im.domain.friendship.service.ImFriendShipGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ImFriendShipGroupController
 * @Description
 * @date 2023/5/5 10:43
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/friendship/group")
public class ImFriendShipGroupController {

    @Resource
    private ImFriendShipGroupService imFriendShipGroupService;

    @Resource
    private ImFriendShipGroupMemberService imFriendShipGroupMemberService;


    @RequestMapping("/add")
    public ResponseVO add(@RequestBody @Validated AddFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupService.addGroup(req);
    }

    @RequestMapping("/del")
    public ResponseVO del(@RequestBody @Validated DeleteFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupService.deleteGroup(req);
    }

    @RequestMapping("/member/add")
    public ResponseVO memberAdd(@RequestBody @Validated AddFriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.addGroupMember(req);
    }

    @RequestMapping("/member/del")
    public ResponseVO memberdel(@RequestBody @Validated DeleteFriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return imFriendShipGroupMemberService.delGroupMember(req);
    }

}
