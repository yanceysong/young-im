package com.yanceysong.im.common.enums.command;

/**
 * @ClassName ConversationEventCommand
 * @Description
 * @date 2023/5/17 13:28
 * @Author yanceysong
 * @Version 1.0
 */
public enum ConversationEventCommand implements Command {

    //删除会话 5000 -> 0x1388
    CONVERSATION_DELETE(0x1388),

    //更新会话 5001 -> 0x1389
    CONVERSATION_UPDATE(0x1389),

    ;

    private final Integer command;

    ConversationEventCommand(int command){
        this.command=command;
    }

    @Override
    public Integer getCommand() {
        return command;
    }

}
