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
     * @param sendId
     * @param receiverId
     * @return
     */
    public static String generateP2PId(String sendId, String receiverId) {
        int i = sendId.compareTo(receiverId);
        if (i < 0) {
            return receiverId + "_" + sendId;
        } else if (i > 0) {
            return sendId + "_" + receiverId;
        }

        throw new RuntimeException("");
    }
}
