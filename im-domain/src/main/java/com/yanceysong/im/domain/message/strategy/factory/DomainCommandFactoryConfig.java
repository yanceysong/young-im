package com.yanceysong.im.domain.message.strategy.factory;

import com.yanceysong.im.common.enums.command.GroupEventCommand;
import com.yanceysong.im.common.enums.command.MessageCommand;
import com.yanceysong.im.domain.message.strategy.DomainCommandStrategy;
import com.yanceysong.im.domain.message.strategy.command.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName DomainCommandFactoryConfig
 * @Description
 * @date 2023/6/8 16:52
 * @Author yanceysong
 * @Version 1.0
 */
@Service
public class DomainCommandFactoryConfig {
    protected final ConcurrentHashMap<Integer, DomainCommandStrategy> commandStrategyMap = new ConcurrentHashMap<>();
    @Resource
    private MessageP2P messageP2P;
    @Resource
    private MessageRead messageRead;
    @Resource
    private MessageRecall messageRecall;
    @Resource
    private MessageReceiveAck messageReceiveAck;
    @Resource
    private MessageGroupRead messageGroupRead;
    @Resource
    private MessageGroup messageGroup;

    public void init() {
        commandStrategyMap.put(MessageCommand.MSG_P2P.getCommand(), messageP2P);
        commandStrategyMap.put(MessageCommand.MSG_RECEIVE_ACK.getCommand(), messageReceiveAck);
        commandStrategyMap.put(MessageCommand.MSG_READ.getCommand(), messageRead);
        commandStrategyMap.put(MessageCommand.MSG_RECALL.getCommand(), messageRecall);
        commandStrategyMap.put(GroupEventCommand.MSG_GROUP.getCommand(), messageGroup);
        commandStrategyMap.put(GroupEventCommand.MSG_GROUP_READ.getCommand(), messageGroupRead);
    }
}
