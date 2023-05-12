package com.yanceysong.im.infrastructure.callback;

import com.yanceysong.im.common.ResponseVO;

/**
 * @ClassName CallbackService
 * @Description 回调机制接口定义
 * @date 2023/5/10 10:08
 * @Author yanceysong
 * @Version 1.0
 */
public interface CallbackService {

    /**
     * 在事件执行之前的回调
     * 干预事件的后续流程处理，以及对用户行为埋点，记录日志
     * 需要返回值(用户有感，异步)
     *
     * @param appId           app的id
     * @param callbackCommand 回调指令
     * @param jsonBody        回调的参数
     * @return 回调的结果
     */
    ResponseVO beforeCallback(Integer appId, String callbackCommand, String jsonBody);

    /**
     * 在事件执行之后的回调
     * 进行数据同步
     * 不需要返回值(用户无感)
     *
     * @param appId           app的id
     * @param callbackCommand 回调指令
     * @param jsonBody        回调的参数
     */
    void afterCallback(Integer appId, String callbackCommand, String jsonBody);

}
