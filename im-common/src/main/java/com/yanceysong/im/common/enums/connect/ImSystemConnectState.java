package com.yanceysong.im.common.enums.connect;

import com.yanceysong.im.common.constant.CodeAdapter;

public enum ImSystemConnectState implements CodeAdapter {
    // 1.在线 2.离线
    CONNECT_STATE_ONLINE(1),
    CONNECT_STATE_OFFLINE(2);

    private final Integer state;

    ImSystemConnectState(Integer state) {
        this.state = state;
    }

    @Override
    public Integer getCode() {
        return state;
    }
}
