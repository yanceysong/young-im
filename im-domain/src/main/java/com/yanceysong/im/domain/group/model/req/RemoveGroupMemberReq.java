package com.yanceysong.im.domain.group.model.req;
import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
/**
 * @ClassName RemoveGroupMemberReq
 * @Description
 * @date 2023/5/5 11:56
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class RemoveGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    private String memberId;

}
