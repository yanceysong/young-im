package com.yanceysong.im.domain.conversation.model;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName DeleteConversationReq
 * @Description
 * @date 2023/5/17 13:40
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= true)
@ToString(doNotUseGetters=true)
public class DeleteConversationReq extends RequestBase {

    @NotBlank(message = "会话 Id 不能为空")
    private String conversationId;

    @NotBlank(message = "fromId 不能为空")
    private String fromId;

}
