package com.yanceysong.im.domain.group.model.req;
import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
/**
 * @ClassName GetGroupReq
 * @Description
 * @date 2023/5/5 11:55
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class GetGroupReq extends RequestBase {

    private String groupId;

}