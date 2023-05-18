package com.yanceysong.im.domain.message.service.check;

import com.yanceysong.im.common.ResponseVO;

/**
 * @ClassName CheckSendMessage
 * @Description
 * @date 2023/5/16 10:41
 * @Author yanceysong
 * @Version 1.0
 */
public interface CheckSendMessage {
    /**
     * 检查发送人是否被禁言或者是禁用
     *
     * @param fromId
     * @param appId
     * @return
     */
    ResponseVO<ResponseVO.NoDataReturn> checkSenderForbidAndMute(String fromId, Integer appId);

    /**
     * 检查好友关系链
     *
     * @param fromId 己方
     * @param toId   对方
     * @param appId  平台 SDK
     * @return
     */
    ResponseVO<ResponseVO.NoDataReturn> checkFriendShip(String fromId, String toId, Integer appId);

    /**
     * 检查群组是否能发送消息
     *
     * @param fromId
     * @param groupId
     * @param appId
     * @return
     */
    ResponseVO<ResponseVO.NoDataReturn> checkGroupMessage(String fromId, String groupId, Integer appId);
}
