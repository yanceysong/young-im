package com.yanceysong.im.domain.user.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.user.model.req.DeleteUserReq;
import com.yanceysong.im.domain.user.model.req.ImportUserReq;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.route.RouteHandler;
import com.yanceysong.im.infrastructure.zookeeper.ZkManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ImUserController
 * @Description
 * @date 2023/5/5 11:20
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/user")
public class ImUserController {
    @Resource
    private ImUserService imUserService;
    @Resource
    private RouteHandler routeHandler;

    @Resource
    private ZkManager zkManager;

    @RequestMapping("importUser")
    public ResponseVO importUser(@RequestBody ImportUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.importUser(req);
    }

    @RequestMapping("/deleteUser")
    public ResponseVO deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }

}