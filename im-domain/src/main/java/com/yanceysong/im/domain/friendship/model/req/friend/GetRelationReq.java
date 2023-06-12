package com.yanceysong.im.domain.friendship.model.req.friend;

import com.yanceysong.im.common.model.common.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName GetRelationReq
 * @Description 获取指定好友
 * @date 2023/5/5 11:01
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class GetRelationReq extends RequestBase {

    @NotBlank(message = "sendId不能为空")
    private String sendId;

    @NotBlank(message = "receiverId不能为空")
    private String receiverId;

}