package com.yanceysong.im.domain.group.model.req.callback;

import com.yanceysong.im.domain.group.model.resp.AddMemberResp;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddMemberAfterCallback
 * @Description
 * @date 2023/5/10 9:48
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class AddMemberAfterCallback {
    private String groupId;
    private Integer groupType;
    private String operator;
    private List<AddMemberResp> memberId;
}

