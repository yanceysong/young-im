package com.yanceysong.im.domain.friendship.model.req;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName GetRelationReq
 * @Description 获取指定好友
 * @date 2023/5/5 11:01
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class GetRelationReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotBlank(message = "toId不能为空")
    private String toId;

}