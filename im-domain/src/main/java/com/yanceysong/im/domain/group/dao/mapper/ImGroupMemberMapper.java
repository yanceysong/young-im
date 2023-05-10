package com.yanceysong.im.domain.group.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yanceysong.im.domain.group.dao.ImGroupMemberEntity;
import com.yanceysong.im.domain.group.model.req.group.GroupMemberDto;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @ClassName ImGroupMemberMapper
 * @Description
 * @date 2023/5/5 11:48
 * @Author yanceysong
 * @Version 1.0
 */
@Repository
public interface ImGroupMemberMapper extends BaseMapper<ImGroupMemberEntity> {

    /**
     * 拉取用户所加入的所有群组 id
     * @param appId
     * @param memberId
     * @return
     */
    List<String> getJoinedGroupId(Integer appId, String memberId);

    /**
     * 拉取群组所有成员(基本)信息
     * @param appId
     * @param groupId
     * @return
     */
    List<GroupMemberDto> getGroupMember(Integer appId, String groupId);

    /**
     * 拉取群组所有成员 id
     * @param appId
     * @param groupId
     * @return
     */
    List<String> getGroupMemberId(Integer appId, String groupId);

    /**
     * 拉取群组管理员(基本)信息
     * @param groupId
     * @param appId
     * @return
     */
    List<GroupMemberDto> getGroupManager(String groupId, Integer appId);

}

