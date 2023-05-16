package com.yanceysong.im.domain.group.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.group.model.req.group.*;
import com.yanceysong.im.domain.group.service.ImGroupService;
import com.yanceysong.im.domain.message.service.GroupMessageService;
import com.yanceysong.im.infrastructure.config.AppConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName ImGroupController
 * @Description
 * @date 2023/5/5 11:44
 * @Author yanceysong
 * @Version 1.0
 */
@Api(tags = "V1群组管理")
@RestController
@RequestMapping("v1/group")
public class ImGroupController {
    @Resource
    private AppConfig appConfig;
    @Resource
    private ImGroupService groupService;

    @Resource
    private GroupMessageService groupMessageService;

    @ApiOperation("测试")
    @GetMapping("/test")
    public String hello() {
        return appConfig.toString();

    }

    /**
     * 导入群组信息
     * <a href="http://localhost:8000/v1/group/importGroup?appId=10001">http://localhost:8000/v1/group/importGroup?appId=10001</a>
     *
     * @param req {
     *            "ownerId":"ld",
     *            "groupName":"1ld测试那",
     *            "groupType":1,
     *            "mute":0,
     *            "joinType":"0",
     *            "privateChat":"0",
     *            "introduction":"",
     *            "notification":"",
     *            "photo":"",
     *            "MaxMemberCount":500
     *            }
     * @return 1.无指定群组 ID --> 自动生成群组 ID
     * {
     * "code": 200,
     * "msg": "success",
     * "data": null,
     * "ok": true
     * }
     * 2.指定 ID --> 报错
     * {
     * "code": 40001,
     * "msg": "群已存在",
     * "data": null,
     * "ok": false
     * }
     */
    @ApiOperation("导入群组信息")
    @PostMapping("/importGroup")
    public ResponseVO importGroup(@RequestBody @Validated ImportGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.importGroup(req);
    }

    /**
     * 创建群组信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @ApiOperation("创建一个群组")
    @PostMapping("/createGroup")
    public ResponseVO createGroup(@RequestBody @Validated CreateGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.createGroup(req);
    }

    /**
     * 获取群组信息
     *
     * @param req   请求
     * @param appId appid
     * @return 返回
     */
    @ApiOperation("获取一个群组信息")
    @GetMapping("/getGroupInfo")
    public ResponseVO getGroupInfo(@RequestBody @Validated GetGroupReq req, Integer appId) {
        req.setAppId(appId);
        return groupService.getGroup(req);
    }

    /**
     * 更新群组信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @ApiOperation("更新一个群组信息")
    @PostMapping("/update")
    public ResponseVO update(@RequestBody @Validated UpdateGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.updateBaseGroupInfo(req);
    }

    /**
     * 获取用户加入的所有群组列表信息
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @ApiOperation("获取用户加入的所有群组列表信息")
    @GetMapping("/getJoinedGroup")
    public ResponseVO getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.getJoinedGroup(req);
    }

    /**
     * 解散群组
     *
     * @param req
     * @param appId
     * @param identifier
     * @return
     */
    @ApiOperation("解散群组")
    @PostMapping("/destroyGroup")
    public ResponseVO destroyGroup(@RequestBody @Validated DestroyGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.destroyGroup(req);
    }

    /**
     * 转让群组
     *
     * @param req        请求
     * @param appId      appid
     * @param identifier 标识符
     * @return 结果
     */
    @ApiOperation("转让群组")
    @PostMapping("/transferGroup")
    public ResponseVO transferGroup(@RequestBody @Validated TransferGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.transferGroup(req);
    }

    @ApiOperation("禁言")
    @PostMapping("/forbidSendMessage")
    public ResponseVO forbidSendMessage(@RequestBody @Validated MuteGroupReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.muteGroup(req);
    }

    @RequestMapping("/sendMessage")
    public ResponseVO sendMessage(@RequestBody @Validated SendGroupMessageReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVO.successResponse(groupMessageService.send(req));
    }

}

