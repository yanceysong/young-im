package com.yanceysong.im.domain.conversation.model;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName UpdateConversationReq
 * @Description
 * @date 2023/5/17 13:40
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= true)
@ToString(doNotUseGetters=true)
public class UpdateConversationReq extends RequestBase {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private String fromId;

}
