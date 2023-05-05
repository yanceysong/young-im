package com.yanceysong.im.domain.group.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.group.model.req.*;
import com.yanceysong.im.domain.group.model.req.MuteGroupReq;
import com.yanceysong.im.domain.group.model.req.TransferGroupReq;
import com.yanceysong.im.domain.group.service.ImGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ImGroupController
 * @Description
 * @date 2023/5/5 11:44
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/group")
public class ImGroupController {

    @Autowired
    private ImGroupService groupService;

    /**
     * 导入群组信息
     * http://localhost:8000/v1/group/importGroup?appId=10001
     * @param req
     * {
     *     "ownerId":"ld",
     *     "groupName":"1ld测试那",
     *     "groupType":1,
     *     "mute":0,
     *     "joinType":"0",
     *     "privateChat":"0",
     *     "introduction":"",
     *     "notification":"",
     *     "photo":"",
     *     "MaxMemberCount":500
     * }
     * @return
     * 1.无指定群组 ID --> 自动生成群组 ID
     * {
     *     "code": 200,
     *     "msg": "success",
     *     "data": null,
     *     "ok": true
     * }
     * 2.指定 ID --> 报错
     * {
     *     "code": 40001,
     *     "msg": "群已存在",
     *     "data": null,
     *     "ok": false
     * }
     */
    @RequestMapping("/importGroup")
    public ResponseVO importGroup(@RequestBody @Validated ImportGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.importGroup(req);
    }

    /**
     * 创建群组信息
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/createGroup")
    public ResponseVO createGroup(@RequestBody @Validated CreateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.createGroup(req);
    }

    /**
     * 获取群组信息
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getGroupInfo")
    public ResponseVO getGroupInfo(@RequestBody @Validated GetGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return groupService.getGroup(req);
    }

    /**
     * 更新群组信息
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/update")
    public ResponseVO update(@RequestBody @Validated UpdateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.updateBaseGroupInfo(req);
    }

    /**
     * 获取用户加入的所有群组列表信息
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/getJoinedGroup")
    public ResponseVO getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.getJoinedGroup(req);
    }

    /**
     * 解散群组
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/destroyGroup")
    public ResponseVO destroyGroup(@RequestBody @Validated DestroyGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.destroyGroup(req);
    }

    /**
     * 转让群组
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @RequestMapping("/transferGroup")
    public ResponseVO transferGroup(@RequestBody @Validated TransferGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.transferGroup(req);
    }

    @RequestMapping("/forbidSendMessage")
    public ResponseVO forbidSendMessage(@RequestBody @Validated MuteGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.muteGroup(req);
    }

}

