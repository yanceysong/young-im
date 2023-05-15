package com.yanceysong.im.domain.group.model.resp;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName AddMemberResp
 * @Description
 * @date 2023/5/5 11:52
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class AddMemberResp {

    private String memberId;

    /**
     * 加入结果：0 为成功；1 为失败；2 为已经是群成员
     */
    private Integer result;

    private String resultMessage;

}
