package com.yanceysong.im.domain.user.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.device.ClientType;
import com.yanceysong.im.domain.user.model.req.*;
import com.yanceysong.im.domain.user.model.resp.ImportUserResp;
import com.yanceysong.im.domain.user.model.resp.UserOnlineStatusResp;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.domain.user.service.state.ImUserStatusService;
import com.yanceysong.im.infrastructure.route.RouteHandler;
import com.yanceysong.im.infrastructure.route.RouteInfo;
import com.yanceysong.im.infrastructure.utils.RouteInfoParseUtil;
import com.yanceysong.im.infrastructure.zookeeper.ZkManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ImUserController
 * @Description
 * @date 2023/5/5 11:20
 * @Author yanceysong
 * @Version 1.0
 */
@Api(tags = "V1用户接口")
@CrossOrigin
@RestController
@RequestMapping("v1/user")
public class ImUserController {
    @Resource
    private ImUserStatusService imUserStatusService;
    @Resource
    private ImUserService imUserService;
    @Resource
    private RouteHandler routeHandler;

    @Resource
    private ZkManager zkManager;

    @ApiOperation("导入用户信息")
    @PostMapping("importUser")
    public ResponseVO<ImportUserResp> importUser(@RequestBody ImportUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.importUser(req);
    }

    @ApiOperation("删除一个用户")
    @PostMapping("/deleteUser")
    public ResponseVO<ImportUserResp> deleteUser(@RequestBody @Validated DeleteUserReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.deleteUser(req);
    }

    /**
     * 客户端向服务端请求该用户各接口需要拉取的数量
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/getUserSequence")
    public ResponseVO<Map<Object, Object>> getUserSequence(@RequestBody @Validated GetUserSequenceReq req, Integer appId) {
        req.setAppId(appId);
        return imUserService.getUserSequence(req);
    }

    /**
     * sdk获取im服务端的地址
     *
     * @param req 登录请求
     * @return im的地址
     */
    @RequestMapping("/login")
    public ResponseVO<String> login(@RequestBody @Validated LoginReq req) {
        ResponseVO<ResponseVO.NoDataReturn> login = imUserService.login(req);
        if (login.isOk()) {
            List<String> allNodes;
            allNodes = req.getClientType().equals(ClientType.WEB.getCode())
                    ? zkManager.getAllWebNode()
                    : zkManager.getAllTcpNode();
            String node = routeHandler.routeServer(allNodes, req.getUserId());
            RouteInfo nodeServer = RouteInfoParseUtil.parse(node);
            return ResponseVO.successResponse("", nodeServer.getIp());
        } else {
            return ResponseVO.errorResponse(login.getCode(), login.getMsg());
        }
    }

    @RequestMapping("/subscribeUserOnlineStatus")
    public ResponseVO<ResponseVO.NoDataReturn> subscribeUserOnlineStatus(@RequestBody @Validated
                                                                         SubscribeUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        imUserStatusService.subscribeUserOnlineStatus(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/setUserCustomerStatus")
    public ResponseVO<ResponseVO.NoDataReturn> setUserCustomerStatus(@RequestBody @Validated
                                                                     SetUserCustomerStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        imUserStatusService.setUserCustomerStatus(req);
        return ResponseVO.successResponse();
    }

    @RequestMapping("/queryFriendOnlineStatus")
    public ResponseVO<Map<String, UserOnlineStatusResp>> queryFriendOnlineStatus(@RequestBody @Validated
                                                                                 PullFriendOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVO.successResponse(imUserStatusService.queryFriendOnlineStatus(req));
    }

    @RequestMapping("/queryUserOnlineStatus")
    public ResponseVO<Map<String, UserOnlineStatusResp>> queryUserOnlineStatus(@RequestBody @Validated
                                                                               PullUserOnlineStatusReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return ResponseVO.successResponse(imUserStatusService.queryUserOnlineStatus(req));
    }
}