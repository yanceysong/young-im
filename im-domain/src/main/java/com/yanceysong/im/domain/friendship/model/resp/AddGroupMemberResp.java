package com.yanceysong.im.domain.friendship.model.resp;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName AddGroupMemberResp
 * @Description
 * @date 2023/5/5 10:51
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@ToString(doNotUseGetters=true)
public class AddGroupMemberResp {
    private List<String> successId;
    private List<String> errorId;
}

