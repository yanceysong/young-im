package com.yanceysong.im.domain.group.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName UpdateGroupMemberReq
 * @Description
 * @date 2023/5/5 11:57
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class UpdateGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    @NotBlank(message = "memberId不能为空")
    private String memberId;

    private String alias;

    private Integer role;

    private String extra;

}