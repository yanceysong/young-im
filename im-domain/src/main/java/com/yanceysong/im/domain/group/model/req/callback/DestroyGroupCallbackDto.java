package com.yanceysong.im.domain.group.model.req.callback;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName DestroyGroupCallbackDto
 * @Description
 * @date 2023/5/10 9:49
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class DestroyGroupCallbackDto {

    private String groupId;
}

