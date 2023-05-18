package com.yanceysong.im.domain.conversation.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.conversation.model.DeleteConversationReq;
import com.yanceysong.im.domain.conversation.model.UpdateConversationReq;
import com.yanceysong.im.domain.conversation.service.ConversationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ConversationController
 * @Description
 * @date 2023/5/17 13:38
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/conversation")
public class ConversationController {

    @Resource
    private ConversationService conversationServiceImpl;

    @RequestMapping("/deleteConversation")
    public ResponseVO deleteConversation(@RequestBody @Validated DeleteConversationReq req,
                                         Integer appId, String identifier) {
        req.setAppId(appId);
//        req.setOperater(identifier);
        return conversationServiceImpl.deleteConversation(req);
    }

    @RequestMapping("/updateConversation")
    public ResponseVO updateConversation(@RequestBody @Validated UpdateConversationReq req,
                                         Integer appId, String identifier) {
        req.setAppId(appId);
//        req.setOperater(identifier);
        return conversationServiceImpl.updateConversation(req);
    }

}