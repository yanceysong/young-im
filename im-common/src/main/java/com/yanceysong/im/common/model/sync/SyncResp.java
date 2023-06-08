package com.yanceysong.im.common.model.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @ClassName SyncResp
 * @Description
 * @date 2023/5/18 14:08
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(doNotUseGetters = true)
public class SyncResp<T> {

    /** 服务端本次拉取的最大 Seq */
    private Long maxSequence;

    /** 是否拉取完成 */
    private boolean isCompleted;

    /** 服务端拉取的所有数据列表 */
    private List<T> dataList;

}
