package com.yanceysong.im.infrastructure.strategy.command;

import com.alibaba.fastjson.JSONObject;
import com.yanceysong.im.codec.proto.Message;
import com.yanceysong.im.common.enums.command.Command;
import com.yanceysong.im.common.model.ClientInfo;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * @ClassName CommandStrategy
 * @Description
 * @date 2023/4/25 10:24
 * @Author yanceysong
 * @Version 1.0
 */
public interface CommandStrategy {
    /**
     * 系统命令执行策略接口
     *
     * @param ctx
     * @param msg
     * @param brokeId
     */
    default void systemStrategy(ChannelHandlerContext ctx, Message msg, Integer brokeId) {

    }

    /**
     * 群组命令执行策略接口
     *
     * @param userId        用户的id
     * @param command       指令
     * @param data
     * @param clientInfo
     * @param groupMemberId
     * @param o
     * @param groupId
     */
    default void groupStrategy(String userId, Command command, Object data, ClientInfo clientInfo,
                               List<String> groupMemberId, JSONObject o, String groupId) {

    }

}
