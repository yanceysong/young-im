package com.yanceysong.im.infrastructure.supports.ids;

/**
 * @ClassName ConversationIdGenerate
 * @Description
 * @date 2023/5/17 11:30
 * @Author yanceysong
 * @Version 1.0
 */
public class ConversationIdGenerate {

    /**
     * 小的 id 放前面
     *
     * @param fromId
     * @param toId
     * @return
     */
    public static String generateP2PId(String fromId, String toId) {
        int i = fromId.compareTo(toId);
        if (i < 0) {
            return toId + "_" + fromId;
        } else if (i > 0) {
            return fromId + "_" + toId;
        }

        throw new RuntimeException("");
    }
}
