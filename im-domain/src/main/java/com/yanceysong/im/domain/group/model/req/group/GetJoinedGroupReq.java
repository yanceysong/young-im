package com.yanceysong.im.domain.group.model.req.group;

import com.yanceysong.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @ClassName GetJoinedGroupReq
 * @Description
 * @date 2023/5/5 11:55
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper=true)
@ToString(doNotUseGetters=true)
public class GetJoinedGroupReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String memberId;

    /**
     * 群类型
     */
    private List<Integer> groupType;

    /**
     * 单次拉取的群组数量，如果不填代表所有群组
     */
    private Integer limit;

    /**
     * 第几页
     */
    private Integer offset;


}

