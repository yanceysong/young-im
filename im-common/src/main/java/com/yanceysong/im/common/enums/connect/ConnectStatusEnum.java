package com.yanceysong.im.common.enums.connect;

/**
 * @ClassName ConnectStatusEnum
 * @Description
 * @date 2023/5/12 10:48
 * @Author yanceysong
 * @Version 1.0
 */
public enum ConnectStatusEnum {
    /**
     * 管道链接状态,1=在线，2=离线。。
     */
    ONLINE_STATUS(1),

    OFFLINE_STATUS(2),
    ;

    private Integer code;

    ConnectStatusEnum(Integer code){
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }
}
