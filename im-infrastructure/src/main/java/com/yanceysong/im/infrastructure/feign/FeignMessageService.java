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
     * RPC 调度业务层的接口，接口职责为检查发送方是否有权限
     *
     * @param req 参数
     * @return 结果
     */
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    @RequestLine("POST /message/checkSend")
    ResponseVO checkSendMessage(CheckSendMessageReq req);

}
