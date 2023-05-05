package com.yanceysong.im.domain.group.model.req;
import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
/**
 * @ClassName DestroyGroupReq
 * @Description
 * @date 2023/5/5 11:54
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DestroyGroupReq extends RequestBase {

    @NotNull(message = "群id不能为空")
    private String groupId;

}
