package com.yanceysong.im.domain.group.model.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName GetRoleInGroupResp
 * @Description
 * @date 2023/5/5 11:53
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@ToString(doNotUseGetters=true)
@EqualsAndHashCode(callSuper= false)
public class GetRoleInGroupResp {

    private Long groupMemberId;

    private String memberId;

    private Integer role;

    private Long speakDate;

}