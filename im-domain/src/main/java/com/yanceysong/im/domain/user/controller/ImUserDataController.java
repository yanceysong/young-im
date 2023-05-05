package com.yanceysong.im.domain.user.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.user.model.req.GetUserInfoReq;
import com.yanceysong.im.domain.user.model.req.ModifyUserInfoReq;
import com.yanceysong.im.domain.user.model.req.UserId;
import com.yanceysong.im.domain.user.service.ImUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName ImUserDataController
 * @Description
 * @date 2023/5/5 11:21
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/user/data")
public class ImUserDataController {

    private static final Logger logger = LoggerFactory.getLogger(ImUserDataController.class);

    @Autowired
    private ImUserService imUserService;

    @GetMapping("/getUserInfo")
    public ResponseVO getUserInfo(@RequestBody GetUserInfoReq req, Integer appId){//@Validated
        req.setAppId(appId);
        return imUserService.getUserInfo(req);
    }

    @PostMapping("/getSingleUserInfo")
    public ResponseVO getSingleUserInfo(@RequestBody @Validated UserId req, Integer appId){
        req.setAppId(appId);
        return imUserService.getSingleUserInfo(req.getUserId(),req.getAppId());
    }

    @PostMapping("/modifyUserInfo")
    public ResponseVO modifyUserInfo(@RequestBody @Validated ModifyUserInfoReq req, Integer appId){
        req.setAppId(appId);
        return imUserService.modifyUserInfo(req);
    }
}
