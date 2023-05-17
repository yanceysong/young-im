package com.yanceysong.im.infrastructure.rabbitmq.listener;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.common.constant.RedisConstants;
import com.yanceysong.im.common.model.UserClientDto;
import com.yanceysong.im.infrastructure.redis.RedisManager;
import com.yanceysong.im.infrastructure.strategy.login.factory.LoginStatusFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;

/**
 * @ClassName UserLoginMessageListener
 * @Description
 * @date 2023/4/27 13:38
 * @Author yanceysong
 * @Version 1.0
 */
@Slf4j
public class UserLoginMessageListener {
    private final Integer loginModel;

    public UserLoginMessageListener(Integer loginModel) {
        this.loginModel = loginModel;
    }

    public void listenerUserLogin() {
        // 监听者监听 UserLoginChannel 队列
        RTopic topic = RedisManager.getRedissonClient().getTopic(RedisConstants.USER_LOGIN_CHANNEL);
        topic.addListener(String.class, (charSequence, msg) -> {
            log.info("收到用户上线通知 {}", msg);
            UserClientDto dto = JSONObject.parseObject(msg, UserClientDto.class);
            LoginStatusFactory loginStatusFactory = new LoginStatusFactory();
            loginStatusFactory.chooseLoginStatus(loginModel);
            loginStatusFactory.handleUserLogin(dto);
        });
    }
}
