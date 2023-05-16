package com.yanceysong.im.domain.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanceysong.im.codec.pack.user.UserModifyPack;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.enums.command.UserEventCommand;
import com.yanceysong.im.common.enums.friend.DelFlagEnum;
import com.yanceysong.im.common.enums.user.UserErrorCode;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.domain.group.service.ImGroupService;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.dao.mapper.ImUserDataMapper;
import com.yanceysong.im.domain.user.model.req.*;
import com.yanceysong.im.domain.user.model.resp.GetUserInfoResp;
import com.yanceysong.im.domain.user.model.resp.ImportUserResp;
import com.yanceysong.im.domain.user.service.ImUserService;
import com.yanceysong.im.infrastructure.callback.CallbackService;
import com.yanceysong.im.infrastructure.config.AppConfig;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName ImUserServiceImpl
 * @Description
 * @date 2023/5/5 11:29
 * @Author yanceysong
 * @Version 1.0
 */
@Service
@Slf4j
public class ImUserServiceImpl implements ImUserService {
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private ImUserDataMapper imUserDataMapper;

    @Resource
    private ImGroupService imGroupService;
    @Resource
    private AppConfig appConfig;
    @Resource
    private CallbackService callbackService;

    @Override
    public ResponseVO importUser(ImportUserReq req) {
        //导入的数量不能太多
        if (req.getUserData().size() > 100) {
            return ResponseVO.errorResponse(UserErrorCode.IMPORT_SIZE_BEYOND);
        }
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        //遍历每一个用户数据,插入到数据库
        for (ImUserDataEntity data : req.getUserData()) {
            try {
                data.setAppId(req.getAppId());
                int insert = imUserDataMapper.insert(data);
                if (insert == 1) {
                    successId.add(data.getUserId());
                }
            } catch (Exception e) {
                log.error("插入数据库失败，用户id:{}", data.getUserId());
                e.printStackTrace();
                errorId.add(data.getUserId());
            }
        }
        return ResponseVO.successResponse(new ImportUserResp(successId, errorId));
    }

    /**
     * 批量获取用户的信息
     *
     * @param req 获取用户信息的请求
     * @return 批量用户信息结果
     */
    @Override
    public ResponseVO getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.in("user_id", req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        List<ImUserDataEntity> userDataEntities = imUserDataMapper.selectList(queryWrapper);
        //从数据库查到的用户集合
        Set<String> dbUserIdSet = userDataEntities
                .stream()
                .map(ImUserDataEntity::getUserId)
                .collect(Collectors.toSet());
        //没有查到的用户id集合
        List<String> failUser = req.getUserIds()
                .stream()
                .filter(userId -> !dbUserIdSet.contains(userId))
                .collect(Collectors.toList());
        return ResponseVO.successResponse(new GetUserInfoResp(userDataEntities, failUser));
    }

    /**
     * 获取单个用户的信息
     *
     * @param userId 用户id
     * @param appId  app的id
     * @return 单个用户的信息
     */
    @Override
    public ResponseVO getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper<ImUserDataEntity>();
        wrapper.eq("app_id", appId);
        wrapper.eq("user_id", userId);
        wrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity imUserDataEntity = imUserDataMapper.selectOne(wrapper);
        return imUserDataEntity != null ? ResponseVO.successResponse(imUserDataEntity)
                : ResponseVO.errorResponse(UserErrorCode.USER_IS_NOT_EXIST);
    }

    /**
     * 批量删除用户的信息
     *
     * @param req 参数 删除用户信息的请求
     * @return 删除结果
     */
    @Override
    public ResponseVO deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();
        //for循环的去更新删除字段是因为要获取成功和失败的userId
        for (String userId : req.getUserId()) {
            QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id", req.getAppId());
            wrapper.eq("user_id", userId);
            wrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            try {
                int updateResult = imUserDataMapper.update(entity, wrapper);
                log.info("删除信息失败，失败的用户id:{}appId:{}", userId, req.getAppId());
                if (updateResult > 0) {
                    successId.add(userId);
                } else {
                    errorId.add(userId);
                }

            } catch (Exception e) {
                errorId.add(userId);
            }
        }

        return ResponseVO.successResponse(new ImportUserResp(successId, errorId));
    }

    /**
     * 修改一个用户的信息
     *
     * @param req 修改后的信息
     * @return 修改结果
     */
    @Override
//    @Transactional
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("user_id", req.getUserId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = imUserDataMapper.selectOne(query);
        if (user == null) {
            log.info("修改用户信息失败，该用户不存在，用户id:{}appId:{}", req.getUserId(), req.getAppId());
            throw new YoungImException(UserErrorCode.USER_IS_NOT_EXIST);
        }
        ImUserDataEntity update = new ImUserDataEntity();
        update.setUserId(req.getUserId());
        update.setNickName(req.getNickName());
        update.setLocation(req.getLocation());
        update.setBirthDay(req.getBirthDay());
        update.setPassword(req.getPassword());
        update.setPhoto(req.getPhoto());
        update.setUserSex(req.getUserSex());
        update.setSelfSignature(req.getSelfSignature());
        update.setFriendAllowType(req.getFriendAllowType());
        update.setAppId(req.getAppId());
        update.setExtra(req.getExtra());
        update.setAppId(null);
        update.setUserId(null);
        //更新
        int updateResult = imUserDataMapper.update(update, query);
        if (updateResult == 1) {
            // 在回调开始前，先发送 TCP 通知，保证数据同步
            UserModifyPack pack = new UserModifyPack();
            pack.setUserId(req.getUserId());
            pack.setNickName(req.getNickName());
            pack.setPassword(req.getPassword());
            pack.setPhoto(req.getPhoto());
            pack.setUserSex(req.getUserSex());
            pack.setSelfSignature(req.getSelfSignature());
            pack.setFriendAllowType(req.getFriendAllowType());

            messageProducer.sendMsgToUser(req.getUserId(), UserEventCommand.USER_MODIFY,
                    pack, req.getAppId(), req.getClientType(), req.getImei());

            // 若修改成功且开启修改用户信息的业务回调，则发起回调
            if (appConfig.isModifyUserAfterCallback()) {
                callbackService.afterCallback(req.getAppId(),
                        Constants.CallbackCommand.MODIFY_USER_AFTER,
                        JSONObject.toJSONString(req));
            }
            return ResponseVO.successResponse();
        }
        return updateResult > 0 ?
                ResponseVO.successResponse() : ResponseVO.errorResponse(UserErrorCode.MODIFY_USER_ERROR);
    }

    /**
     * @param req 登录的请求
     * @return 登录的结果
     */
    @Override
    public ResponseVO login(LoginReq req) {
        // TODO 后期补充鉴权
        return ResponseVO.successResponse();
    }

}