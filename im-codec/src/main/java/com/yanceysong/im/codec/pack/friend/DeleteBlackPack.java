package com.yanceysong.im.codec.pack.friend;

import lombok.Data;

/**
 * @ClassName DeleteBlackPack
 * @Description 删除黑名单通知报文
 * @date 2023/5/12 13:37
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DeleteBlackPack {
    private String fromId;

    private String toId;

    private Long sequence;

}
