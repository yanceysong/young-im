package com.yanceysong.im.codec.pack.user;

import lombok.Data;

/**
 * @ClassName UserCustomStatusChangeNotifyPack
 * @Description
 * @date 2023/6/8 12:35
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UserCustomStatusChangeNotifyPack {
    private String customText;

    private Integer customStatus;

    private String userId;
}
