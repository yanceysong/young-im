package com.yanceysong.im.domain.user.model.resp;

import com.yanceysong.im.common.model.UserSession;
import lombok.Data;

import java.util.List;

/**
 * @ClassName UserOnlineStatusResp
 * @Description
 * @date 2023/5/5 11:25
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UserOnlineStatusResp {

    private List<UserSession> session;

    private String customText;

    private Integer customStatus;

}
