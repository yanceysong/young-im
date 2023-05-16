package com.yanceysong.im.domain.message.model.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName SendMessageResp
 * @Description
 * @date 2023/5/16 10:38
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class SendMessageResp {

    private Long messageKey;

    private Long messageTime;

}
