package com.yanceysong.im.infrastructure.session;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.common.constant.Constants;
import com.yanceysong.im.common.enums.connect.ConnectStatusEnum;
import com.yanceysong.im.common.model.UserSession;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName UserSessionServiceImpl
 * @Description
 * @date 2023/5/12 10:57
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class UserSessionServiceImpl implements UserSessionService {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public List<UserSession> getUserSession(Integer appId, String userId) {
        String userSessionKey = appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(userSessionKey);
        return entries.values().stream()
                .map(Object::toString)
                .map(value -> JSONObject.parseObject(value, UserSession.class))
                .filter(userSession -> ConnectStatusEnum.ONLINE_STATUS.getCode()
                        .equals(userSession.getConnectState()))
                .collect(Collectors.toList());
    }

    @Override
    public UserSession getUserSession(Integer appId, String userId, Integer clientType, String imei) {
        String userSessionKey = appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId;
        String hashKey = clientType + imei;
        // 通过 userSessionKey 获取用户的 Session map 集合，再通过 hashKey 键值寻找到指定的端 Session value 值
        Object value = redisTemplate.opsForHash().get(userSessionKey, hashKey);
        assert value != null;
        return JSONObject.parseObject(value.toString(), UserSession.class);
    }
}
