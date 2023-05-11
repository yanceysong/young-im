package com.yanceysong.im.domain.group.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.group.model.req.group.*;
import com.yanceysong.im.domain.group.service.ImGroupMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ImGroupMemberController
 * @Description
 * @date 2023/5/5 11:45
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/group/member")
public class ImGroupMemberController {

    @Resource
    private ImGroupMemberService groupMemberService;

    @RequestMapping("/importGroupMember")
    public ResponseVO importGroupMember(@RequestBody @Validated ImportGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.importGroupMember(req);
    }

    @RequestMapping("/add")
    public ResponseVO addMember(@RequestBody @Validated AddGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.addMember(req);
    }

    @RequestMapping("/remove")
    public ResponseVO removeMember(@RequestBody @Validated RemoveGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.removeMember(req);
    }

    @RequestMapping("/update")
    public ResponseVO updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.updateGroupMember(req);
    }

    @RequestMapping("/speak")
    public ResponseVO speak(@RequestBody @Validated SpeaMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.speak(req);
    }

}

