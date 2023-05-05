package com.yanceysong.im.domain.user.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @ClassName DeleteUserReq
 * @Description
 * @date 2023/5/5 11:22
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DeleteUserReq extends RequestBase {

    @NotEmpty(message = "用户id不能为空")
    private List<String> userId;
}