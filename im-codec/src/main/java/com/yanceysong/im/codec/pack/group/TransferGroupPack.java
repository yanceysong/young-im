package com.yanceysong.im.codec.pack.group;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName TransferGroupPack
 * @Description 转让群主通知报文
 * @date 2023/5/12 13:41
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class TransferGroupPack {

    private String groupId;

    private String ownerId;

}