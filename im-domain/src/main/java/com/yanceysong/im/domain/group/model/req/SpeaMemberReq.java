package com.yanceysong.im.domain.group.model.req;
import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SpeaMemberReq
 * @Description
 * @date 2023/5/5 11:57
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class SpeaMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    @NotBlank(message = "memberId不能为空")
    private String memberId;

    //禁言时间，单位毫秒
    @NotNull(message = "禁言时间不能为空")
    private Long speakDate;
}
