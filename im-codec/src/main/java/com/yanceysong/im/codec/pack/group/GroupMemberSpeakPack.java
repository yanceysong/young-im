package com.yanceysong.im.codec.pack.group;

import lombok.Data;

/**
 * @ClassName GroupMemberSpeakPack
 * @Description
 * @date 2023/5/12 13:40
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class GroupMemberSpeakPack {

    private String groupId;

    private String memberId;

    private Long speakDate;

}
