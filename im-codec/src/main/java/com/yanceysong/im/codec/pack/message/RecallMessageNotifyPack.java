package com.yanceysong.im.codec.pack.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @ClassName RecallMessageNotifyPack
 * @Description
 * @date 2023/6/8 15:39
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@ToString(doNotUseGetters = true)
public class RecallMessageNotifyPack {

    private String sendId;

    private String receiverId;

    private Long messageKey;

    private Long messageSequence;
}