package com.yanceysong.im.domain.friendship.controller;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.model.sync.SyncReq;
import com.yanceysong.im.domain.friendship.model.req.friend.*;
import com.yanceysong.im.domain.friendship.service.ImFriendService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName ImFriendShipController
 * @Description
 * @date 2023/5/5 10:39
 * @Author yanceysong
 * @Version 1.0
 */
@RestController
@RequestMapping("v1/friendship")
public class ImFriendShipController {
    @Resource
    private ImFriendService imFriendShipService;

    /**
     * <a href="http://localhost:8000/v1/friendship/importFriendShip?appId=10001">http://localhost:8000/v1/friendship/importFriendShip?appId=10001</a>
     * @param req
     * {
     *     "sendId":"lld2",
     *     "friendItem":[
     *         {
     *             "remark":"备注",
     *             "receiverId":"123456"
     *         }
     *     ]
     * }
     * @return
     * 第一次插入(成功)：{
     *     "code": 200,
     *     "msg": "success",
     *     "data": { "successId": ["123456"],"errorId": []},
     *     "ok": true
     * }
     * 第二次插入(失败)：{
     *     "code": 200,
     *     "msg": "success",
     *     "data": { "successId": [],"errorId": ["123456"]},
     *     "ok": true
     * }
     */
    @PostMapping("/importFriendShip")
    public ResponseVO importFriendShip(@RequestBody @Validated ImportFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.importFriendShip(req);
    }

    @RequestMapping("/addFriend")
    public ResponseVO addFriend(@RequestBody @Validated AddFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.addFriend(req);
    }

    @RequestMapping("/updateFriend")
    public ResponseVO updateFriend(@RequestBody @Validated UpdateFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.updateFriend(req);
    }

    @RequestMapping("/deleteFriend")
    public ResponseVO<ResponseVO.NoDataReturn> deleteFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.deleteFriend(req);
    }

    @RequestMapping("/deleteAllFriend")
    public ResponseVO deleteAllFriend(@RequestBody @Validated DeleteFriendReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.deleteAllFriend(req);
    }

    @RequestMapping("/getAllFriendShip")
    public ResponseVO getAllFriendShip(@RequestBody @Validated GetAllFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.getAllFriendShip(req);
    }

    @RequestMapping("/getRelation")
    public ResponseVO getRelation(@RequestBody @Validated GetRelationReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.getRelation(req);
    }

    @PostMapping("/checkFriend")
    public ResponseVO checkFriend(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.checkFriendship(req);
    }

    @RequestMapping("/addBlack")
    public ResponseVO addBlack(@RequestBody @Validated AddFriendShipBlackReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.addBlack(req);
    }

    @RequestMapping("/deleteBlack")
    public ResponseVO deleteBlack(@RequestBody @Validated DeleteBlackReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.deleteBlack(req);
    }

    @RequestMapping("/checkBlck")
    public ResponseVO checkBlck(@RequestBody @Validated CheckFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.checkBlck(req);
    }
    /**
     * 同步好友列表
     * @param req
     * @param appId
     * @return
     */
    @RequestMapping("/syncFriendShipList")
    public ResponseVO syncFriendShipList(@RequestBody @Validated SyncReq req, Integer appId){
        req.setAppId(appId);
        return imFriendShipService.syncFriendShipList(req);
    }
}
