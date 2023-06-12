package com.yanceysong.im.domain.friendship.service;

import com.yanceysong.im.common.ResponseVO;
import com.yanceysong.im.domain.friendship.model.req.friend.AddFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.req.friend.DeleteFriendShipGroupMemberReq;
import com.yanceysong.im.domain.friendship.model.resp.AddGroupMemberResp;

import java.util.List;

/**
 * @ClassName ImFriendShipGroupMemberService
 * @Description
 * @date 2023/5/5 11:02
 * @Author yanceysong
 * @Version 1.0
 */
public interface ImFriendShipGroupMemberService {

    ResponseVO<AddGroupMemberResp> addGroupMember(AddFriendShipGroupMemberReq req);

    ResponseVO<List<String>> delGroupMember(DeleteFriendShipGroupMemberReq req);

    int doAddGroupMember(Long groupId, String receiverId);

    int clearGroupMember(Long groupId);
}

