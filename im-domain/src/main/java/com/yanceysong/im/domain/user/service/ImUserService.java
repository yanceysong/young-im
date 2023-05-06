package com.yanceysong.im.domain.user.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.user.model.req.*;

/**
 * @ClassName ImUserService
 * @Description
 * @date 2023/5/5 11:28
 * @Author yanceysong
 * @Version 1.0
 */
public interface ImUserService {

    /**
     * 批量导入用户信息
     *
     * @param req 请求
     * @return 结果
     */
    ResponseVO importUser(ImportUserReq req);

    ResponseVO getUserInfo(GetUserInfoReq req);

    /**
     * 获取单个用户信息
     *
     * @param userId 用户id
     * @param appId  app的id
     * @return 单个用户信息
     */
    ResponseVO getSingleUserInfo(String userId, Integer appId);

    /**
     * 删除一个用户（假删除，标记位）
     *
     * @param req 参数
     * @return 结果
     */
    ResponseVO deleteUser(DeleteUserReq req);

    /**
     * 修改一个用户的信息
     *
     * @param req 修改后的信息
     * @return 修改结果
     */
    ResponseVO modifyUserInfo(ModifyUserInfoReq req);

    ResponseVO login(LoginReq req);
}