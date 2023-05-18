package com.yanceysong.im.common.model.store;

import com.yanceysong.im.common.model.content.MessageBody;
import com.yanceysong.im.common.model.content.MessageContent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName DoStoreP2PMessageDto
 * @Description
 * @date 2023/5/16 9:55
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class DoStoreP2PMessageDto {
    private MessageContent messageContent;

    private MessageBody messageBody;
}
