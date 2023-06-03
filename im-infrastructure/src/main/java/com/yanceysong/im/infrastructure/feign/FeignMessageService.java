package com.yanceysong.im.infrastructure.feign;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.model.CheckSendMessageReq;
import feign.Headers;
import feign.RequestLine;

/**
 * @ClassName FeignMessageService
 * @Description
 * @date 2023/5/16 11:21
 * @Author yanceysong
 * @Version 1.0
 */
public interface FeignMessageService {

    /**
     * RPC 调度业务层的接口，接口职责为检查 [P2P] 发送方是否有权限
     * @param o
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /message/p2pCheckSend")
    ResponseVO<ResponseVO.NoDataReturn> checkP2PSendMessage(CheckSendMessageReq o);

    /**
     * RPC 调度业务层的接口，接口职责为检查 [GROUP] 发送方是否有权限
     * @param o
     * @return
     */
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @RequestLine("POST /message/groupCheckSend")
    ResponseVO<ResponseVO.NoDataReturn> checkGroupSendMessage(CheckSendMessageReq o);

}
