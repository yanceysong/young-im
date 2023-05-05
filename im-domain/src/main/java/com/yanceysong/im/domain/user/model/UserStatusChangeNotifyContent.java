package com.yanceysong.im.domain.user.model;

import com.yanceysong.im.common.model.ClientInfo;
import lombok.Data;

/**
 * @ClassName UserStatusChangeNotifyContent
 * @Description
 * @date 2023/5/5 11:26
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UserStatusChangeNotifyContent extends ClientInfo {


    private String userId;

    /**
     * 服务端状态 1上线 2离线
     */
    private Integer status;

}