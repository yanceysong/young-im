package com.yanceysong.im.domain.message.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.model.CheckSendMessageReq;
import com.yanceysong.im.domain.message.model.req.SendMessageReq;
import com.yanceysong.im.domain.message.service.GroupMessageService;
import com.yanceysong.im.domain.message.service.P2PMessageService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName MessageController
 * @Description 后台发送消息控制层
 * @date 2023/5/16 10:34
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/message")
public class MessageController {

    @Resource
    private P2PMessageService p2PMessageService;
    @Resource
    private GroupMessageService groupMessageService;

    /**
     * 后台消息发送接口
     *
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/send")
    public ResponseVO send(@RequestBody @Validated SendMessageReq req, Integer appId) {
        req.setAppId(appId);
        return ResponseVO.successResponse(p2PMessageService.send(req));
    }

    /**
     * Feign RPC 调用内部接口
     *
     * @param req
     * @return
     */
    @RequestMapping("/p2pCheckSend")
    public ResponseVO checkSend(@RequestBody @Validated CheckSendMessageReq req) {
        return p2PMessageService.serverPermissionCheck(req.getFromId(), req.getToId(), req.getAppId());
    }
    /**
     * Feign RPC 调用 [GROUP] 内部接口
     * @param req
     * @return
     */
    @RequestMapping("/groupCheckSend")
    public ResponseVO checkGroupSend(@RequestBody @Validated CheckSendMessageReq req) {
        return groupMessageService.serverPermissionCheck(
                req.getFromId(), req.getToId(), req.getAppId());
    }

}

