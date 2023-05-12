package com.yanceysong.im.codec.pack.group;

import lombok.Data;

/**
 * @ClassName DestroyGroupPack
 * @Description
 * @date 2023/5/12 13:40
 * @Author yanceysong
 * @Version 1.0
 */
@Data
public class DestroyGroupPack {

    private String groupId;

    private Long sequence;

}
