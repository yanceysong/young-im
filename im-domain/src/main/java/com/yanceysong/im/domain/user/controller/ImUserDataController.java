package com.yanceysong.im.domain.user.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.user.model.req.GetUserInfoReq;
import com.yanceysong.im.domain.user.model.req.ModifyUserInfoReq;
import com.yanceysong.im.domain.user.model.req.UserId;
import com.yanceysong.im.domain.user.service.ImUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @ClassName ImUserDataController
 * @Description
 * @date 2023/5/5 11:21
 * @Author yanceysong
 * @Version 1.0
 */

@Api(tags = "V1用户数据接口")
@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {

    @Resource
    private ImUserService imUserService;
    @ApiOperation("批量获取用户信息")
    @GetMapping("/getUserInfo")
    public ResponseVO getUserInfo(@RequestBody GetUserInfoReq req, Integer appId) {
//        req.setAppId(appId);
        return imUserService.getUserInfo(req);
    }
    @ApiOperation("获取单个用户信息")
    @PostMapping("/getSingleUserInfo")
    public ResponseVO getSingleUserInfo(@RequestBody @Validated UserId req, Integer appId) {
//        req.setAppId(appId);
        return imUserService.getSingleUserInfo(req.getUserId(), req.getAppId());
    }
    @ApiOperation("修改一个用户信息")
    @PostMapping("/modifyUserInfo")
    public ResponseVO modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId) {
//        req.setAppId(appId);
        return imUserService.modifyUserInfo(req);
    }
}
