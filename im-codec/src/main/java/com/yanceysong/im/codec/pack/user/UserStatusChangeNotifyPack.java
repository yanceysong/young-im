package com.yanceysong.im.codec.pack.user;

import com.yanceysong.im.common.model.user.UserSession;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName UserStatusChangeNotifyPack
 * @Description
 * @date 2023/6/8 11:49
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@ToString(doNotUseGetters = true)
public class UserStatusChangeNotifyPack {
    private Integer appId;

    private String userId;

    private Integer status;

    private List<UserSession> client;
}
