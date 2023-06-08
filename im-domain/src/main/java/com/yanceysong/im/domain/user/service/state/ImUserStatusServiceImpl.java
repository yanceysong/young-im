package com.yanceysong.im.domain.user.service.state;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.pack.user.UserCustomStatusChangeNotifyPack;
import com.yanceysong.im.codec.pack.user.UserStatusChangeNotifyPack;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.enums.command.UserEventCommand;
import com.yanceysong.im.common.model.common.ClientInfo;
import com.yanceysong.im.common.model.user.UserSession;
import com.yanceysong.im.domain.friendship.service.ImFriendService;
import com.yanceysong.im.domain.user.model.UserStatusChangeNotifyContent;
import com.yanceysong.im.domain.user.model.req.PullFriendOnlineStatusReq;
import com.yanceysong.im.domain.user.model.req.PullUserOnlineStatusReq;
import com.yanceysong.im.domain.user.model.req.SetUserCustomerStatusReq;
import com.yanceysong.im.domain.user.model.req.SubscribeUserOnlineStatusReq;
import com.yanceysong.im.domain.user.model.resp.UserOnlineStatusResp;
import com.yanceysong.im.infrastructure.sendMsg.MessageProducer;
import com.yanceysong.im.infrastructure.session.UserSessionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName ImUserStatusServiceImpl
 * @Description
 * @date 2023/6/8 11:45
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class ImUserStatusServiceImpl implements ImUserStatusService {
    @Resource
    private UserSessionService userSessionServiceImpl;

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private ImFriendService imFriendService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content) {
        List<UserSession> userSession = userSessionServiceImpl.getUserSession(content.getAppId(), content.getUserId());
        UserStatusChangeNotifyPack userStatusChangeNotifyPack = new UserStatusChangeNotifyPack();
        BeanUtils.copyProperties(content, userStatusChangeNotifyPack);
        userStatusChangeNotifyPack.setClient(userSession);
        //发送给自己的同步端
        syncSender(userStatusChangeNotifyPack, content.getUserId(), content);
        //同步给好友和订阅了自己的人
        dispatcher(userStatusChangeNotifyPack, content.getUserId(), content.getAppId());
    }


    private void syncSender(Object pack, String userId, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(userId,
                UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC,
                pack, clientInfo);
    }

    private void dispatcher(Object pack, String userId, Integer appId) {
        //获取自己的所有好友的id
        List<String> allFriendId = imFriendService.getAllFriendId(userId, appId);
        for (String fid : allFriendId) {
            //发送给所有的好友
            messageProducer.sendToUserAllClient(fid, UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY,
                    pack, appId);
        }
        // 发送给临时订阅的人
        String userKey = appId + ":" + RedisConstants.SUBSCRIBE + userId;
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(userKey);
        for (Object key : keys) {
            String filed = (String) key;
            long expire = Long.parseLong((String) stringRedisTemplate.opsForHash().get(userKey, filed));
            if (expire > 0 && expire > System.currentTimeMillis()) {
                messageProducer.sendToUserAllClient(filed, UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY,
                        pack, appId);
            } else {
                stringRedisTemplate.opsForHash().delete(userKey, filed);
            }
        }
    }


    /**
     * @param
     * @return void
     * @description:
     * @author lld
     */
    @Override
    public void subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req) {
        //hash
        // B - [A:xxxx,C:xxxx]
        // C - []
        // D - []
        long subExpireTime = 0L;
        if (req != null && req.getSubTime() > 0) {
            subExpireTime = System.currentTimeMillis() + req.getSubTime();
        }
        if (req != null) {
            for (String beSubUserId : req.getSubUserId()) {
                String userKey = req.getAppId() + ":" + RedisConstants.SUBSCRIBE + ":" + beSubUserId;
                stringRedisTemplate.opsForHash().put(userKey, req.getOperator(), Long.toString(subExpireTime));
            }
        }
    }

    /**
     * @param
     * @return void
     * @description: 设置自定义状态
     * @author lld
     */
    @Override
    public void setUserCustomerStatus(SetUserCustomerStatusReq req) {
        UserCustomStatusChangeNotifyPack userCustomStatusChangeNotifyPack = new UserCustomStatusChangeNotifyPack();
        userCustomStatusChangeNotifyPack.setCustomStatus(req.getCustomStatus());
        userCustomStatusChangeNotifyPack.setCustomText(req.getCustomText());
        userCustomStatusChangeNotifyPack.setUserId(req.getUserId());
        stringRedisTemplate.opsForValue().set(req.getAppId()
                        + ":" + RedisConstants.USER_CUSTOMER_STATUS + ":" + req.getUserId()
                , JSONObject.toJSONString(userCustomStatusChangeNotifyPack));

        syncSender(userCustomStatusChangeNotifyPack,
                req.getUserId(), new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        dispatcher(userCustomStatusChangeNotifyPack, req.getUserId(), req.getAppId());
    }

    @Override
    public Map<String, UserOnlineStatusResp> queryFriendOnlineStatus(PullFriendOnlineStatusReq req) {

        List<String> allFriendId = imFriendService.getAllFriendId(req.getOperator(), req.getAppId());
        return getUserOnlineStatus(allFriendId, req.getAppId());
    }

    @Override
    public Map<String, UserOnlineStatusResp> queryUserOnlineStatus(PullUserOnlineStatusReq req) {
        return getUserOnlineStatus(req.getUserList(), req.getAppId());
    }

    private Map<String, UserOnlineStatusResp> getUserOnlineStatus(List<String> userId, Integer appId) {

        Map<String, UserOnlineStatusResp> result = new HashMap<>(userId.size());
        for (String uid : userId) {

            UserOnlineStatusResp resp = new UserOnlineStatusResp();
            List<UserSession> userSession = userSessionServiceImpl.getUserSession(appId, uid);
            resp.setSession(userSession);
            String userKey = appId + ":" + RedisConstants.USER_CUSTOMER_STATUS + ":" + uid;
            String s = stringRedisTemplate.opsForValue().get(userKey);
            if (StringUtils.isNotBlank(s)) {
                JSONObject parse = (JSONObject) JSON.parse(s);
                resp.setCustomText(parse.getString("customText"));
                resp.setCustomStatus(parse.getInteger("customStatus"));
            }
            result.put(uid, resp);
        }
        return result;
    }
}
