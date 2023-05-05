package com.yanceysong.im.domain.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.common.enums.friend.DelFlagEnum;
import com.yanceysong.im.common.enums.user.UserErrorCode;
import com.yanceysong.im.common.exception.YoungImException;
import com.yanceysong.im.domain.group.service.ImGroupService;
import com.yanceysong.im.domain.user.dao.ImUserDataEntity;
import com.yanceysong.im.domain.user.dao.mapper.ImUserDataMapper;
import com.yanceysong.im.domain.user.model.req.DeleteUserReq;
import com.yanceysong.im.domain.user.model.req.GetUserInfoReq;
import com.yanceysong.im.domain.user.model.req.ImportUserReq;
import com.yanceysong.im.domain.user.model.req.ModifyUserInfoReq;
import com.yanceysong.im.domain.user.model.resp.GetUserInfoResp;
import com.yanceysong.im.domain.user.model.resp.ImportUserResp;
import com.yanceysong.im.domain.user.service.ImUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ImUserDataMapper imUserDataMapper;

    @Autowired
    private ImGroupService imGroupService;

    @Override
    public ResponseVO importUser(ImportUserReq req) {
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
                log.error("插入数据库失败，用户id{}", data.getUserId());
                e.printStackTrace();
                errorId.add(data.getUserId());
            }
        }

        return ResponseVO.successResponse(new ImportUserResp(successId,errorId));
    }

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

    @Override
    public ResponseVO deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();
        //这里改成in操作 todo
        for (String userId : req.getUserId()) {
            QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id", req.getAppId());
            wrapper.eq("user_id", userId);
            wrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            try {
                int update = imUserDataMapper.update(entity, wrapper);
                if (update > 0) {
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

    @Override
    @Transactional
    public ResponseVO modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("user_id", req.getUserId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = imUserDataMapper.selectOne(query);
        if (user == null) {
            throw new YoungImException(UserErrorCode.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req, update);

        update.setAppId(null);
        update.setUserId(null);
        //更新
        return imUserDataMapper.update(update, query) > 0 ?
                ResponseVO.successResponse() : ResponseVO.errorResponse(UserErrorCode.MODIFY_USER_ERROR);

    }

}