package com.yanceysong.im.domain.group.model.req.group;
import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class GetGroupReq extends RequestBase {

    private String groupId;

}