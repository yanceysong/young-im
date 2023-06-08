package com.yanceysong.im.domain.friendship.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.model.common.RequestBase;
import com.yanceysong.im.common.model.sync.SyncReq;
import com.yanceysong.im.domain.friendship.dao.ImFriendShipEntity;
import com.yanceysong.im.domain.friendship.model.req.friend.*;
import com.yanceysong.im.domain.friendship.model.resp.CheckFriendShipResp;
import com.yanceysong.im.domain.friendship.model.resp.ImportFriendShipResp;

import java.util.List;

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
    ResponseVO<ImportFriendShipResp> importFriendShip(ImportFriendShipReq req);

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

    ResponseVO<ResponseVO.NoDataReturn> updateFriend(UpdateFriendReq req);

    ResponseVO<ResponseVO.NoDataReturn> deleteFriend(DeleteFriendReq req);

    ResponseVO<ResponseVO.NoDataReturn> deleteAllFriend(DeleteFriendReq req);

    /**
     * 查询所有好友关系
     *
     * @param req fromId
     * @return
     */
    ResponseVO<List<ImFriendShipEntity>> getAllFriendShip(GetAllFriendShipReq req);

    /**
     * 查询指定好友关系  [是否落库持久化]
     *
     * @param req fromId、toId
     * @return
     */
    ResponseVO<ImFriendShipEntity> getRelation(GetRelationReq req);

    ResponseVO<ResponseVO.NoDataReturn> doAddFriend(RequestBase requestBase, String fromId, FriendDto dto, Integer appId);

    ResponseVO<List<CheckFriendShipResp>> checkFriendship(CheckFriendShipReq req);

    ResponseVO<ResponseVO.NoDataReturn> addBlack(AddFriendShipBlackReq req);

    ResponseVO<ResponseVO.NoDataReturn> deleteBlack(DeleteBlackReq req);

    ResponseVO<List<CheckFriendShipResp>> checkBlck(CheckFriendShipReq req);
    /**
     * 增量拉取好友关系
     * @param req
     * @return
     */
    ResponseVO syncFriendShipList(SyncReq req);
    List<String> getAllFriendId(String userId, Integer appId);

}
