package com.yanceysong.im.domain.user.service.state;

import com.yanceysong.im.domain.user.model.UserStatusChangeNotifyContent;
import com.yanceysong.im.domain.user.model.req.PullFriendOnlineStatusReq;
import com.yanceysong.im.domain.user.model.req.PullUserOnlineStatusReq;
import com.yanceysong.im.domain.user.model.req.SetUserCustomerStatusReq;
import com.yanceysong.im.domain.user.model.req.SubscribeUserOnlineStatusReq;
import com.yanceysong.im.domain.user.model.resp.UserOnlineStatusResp;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName ImUserStatusService
 * @Description
 * @date 2023/6/8 11:45
 * @Author yanceysong
 * @Version 1.0
 */
public interface ImUserStatusService {
    void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content);

    void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req);

    void setUserCustomerStatus(SetUserCustomerStatusReq req);

    Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req);

    Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req);
}
