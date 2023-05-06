package com.yanceysong.im.domain.friendship.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.model.RequestBase;
import com.yanceysong.im.domain.friendship.model.req.*;

/**
 * @ClassName ImFriendService
 * @Description
 * @date 2023/5/5 10:41
 * @Author yanceysong
 * @Version 1.0
 */
public interface ImFriendService {
    /**
     * 导入其他系统的好友关系
     *
     * @param req 关系请求
     * @return 导入结果
     */
    ResponseVO importFriendShip(ImportFriendShipReq req);

    /**
     * 添加好友逻辑
     *
     * @param req 添加好友的请求
     * @return 添加结果
     */
    ResponseVO addFriend(AddFriendReq req);

    /**
     * 更新好友
     *
     * @param req 请求
     * @return 结果
     */

    ResponseVO updateFriend(UpdateFriendReq req);

    ResponseVO deleteFriend(DeleteFriendReq req);

    ResponseVO deleteAllFriend(DeleteFriendReq req);

    /**
     * 查询所有好友关系
     *
     * @param req fromId
     * @return
     */
    ResponseVO getAllFriendShip(GetAllFriendShipReq req);

    /**
     * 查询指定好友关系
     *
     * @param req fromId、toId
     * @return
     */
    ResponseVO getRelation(GetRelationReq req);

    ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);

    ResponseVO checkFriendship(CheckFriendShipReq req);

    ResponseVO addBlack(AddFriendShipBlackReq req);

    ResponseVO deleteBlack(DeleteBlackReq req);

    ResponseVO checkBlck(CheckFriendShipReq req);

}
