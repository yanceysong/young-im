package com.yanceysong.im.infrastructure.rabbitmq.process;

import com.yanceysong.im.codec.proto.MessagePack;
import com.yanceysong.im.infrastructure.utils.UserChannelRepository;
import io.netty.channel.Channel;

/**
 * @ClassName BaseProcess
 * @Description
 * @date 2023/5/15 10:36
 * @Author yanceysong
 * @Version 1.0
 */
public abstract class BaseProcess {

    public void process(MessagePack messagePack) {
        processBefore();

        Channel userChannel = UserChannelRepository.getUserChannel(messagePack.getAppId(),
                messagePack.getReceiverId(), messagePack.getClientType(), messagePack.getImei());
        if (userChannel != null) {
            // 数据通道写入消息内容
            userChannel.writeAndFlush(messagePack);
        }

        processAfter();
    }

    /**
     * 流程执行前的定制化处理
     */
    public abstract void processBefore();

    /**
     * 流程执行后的定制化处理
     */
    public abstract void processAfter();

}
