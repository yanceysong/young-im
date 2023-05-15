package com.yanceysong.im.domain.group.model.req.group;
import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
/**
 * @ClassName GroupMemberDto
 * @Description
 * @date 2023/5/5 11:55
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class GroupMemberDto {

    private String memberId;

    private String alias;

    private Integer role;//群成员类型，0 普通成员, 1 管理员, 2 群主， 3 已经移除的成员，当修改群成员信息时，只能取值0/1，其他值由其他接口实现，暂不支持3

//    private Integer speakFlag;

    private Long speakDate;

    private String joinType;

    private Long joinTime;

}

