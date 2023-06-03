package com.yanceysong.im.infrastructure.strategy.login.factory;

import com.yanceysong.im.common.enums.device.DeviceMultiLoginEnum;
import com.yanceysong.im.infrastructure.strategy.login.LoginStatus;
import com.yanceysong.im.infrastructure.strategy.login.impl.AllClientLoginStatus;
import com.yanceysong.im.infrastructure.strategy.login.impl.OneClientLoginStatus;
import com.yanceysong.im.infrastructure.strategy.login.impl.ThreeClientLoginStatus;
import com.yanceysong.im.infrastructure.strategy.login.impl.TwoClientLoginStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName LoginStatusFactoryConfig
 * @Description
 * @date 2023/4/27 13:55
 * @Author yanceysong
 * @Version 1.0
 */
public class LoginStatusFactoryConfig {
    public static Map<Integer, LoginStatus> LoginStatusMap = new ConcurrentHashMap<>();

    public LoginStatusFactoryConfig() {
        init();
    }

    public static void init() {
        LoginStatusMap.put(DeviceMultiLoginEnum.ONE.getCode(), new OneClientLoginStatus());
        LoginStatusMap.put(DeviceMultiLoginEnum.TWO.getCode(), new TwoClientLoginStatus());
        LoginStatusMap.put(DeviceMultiLoginEnum.THREE.getCode(), new ThreeClientLoginStatus());
        LoginStatusMap.put(DeviceMultiLoginEnum.ALL.getCode(), new AllClientLoginStatus());
    }
}
