package com.yanceysong.im.domain.group.model.req.group;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.common.ClientInfo;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName GroupMsgReq
 * @Description
 * @date 2023/5/16 10:08
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@Builder
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class GroupMsgReq {
    private String userId;

    private Command command;

    private Object data;

    private ClientInfo clientInfo;

    private List<String> groupMemberId;

    private JSONObject o;

    private String groupId;
}
