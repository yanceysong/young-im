package com.yanceysong.im.common.enums.device;

import com.yanceysong.im.common.constant.CodeAdapter;

public enum ConnectState implements CodeAdapter {
    // 1.在线 2.离线
    CONNECT_STATE_ONLINE(1),
    CONNECT_STATE_OFFLINE(2);

    private final Integer state;

    ConnectState(Integer state) {
        this.state = state;
    }

    @Override
    public Integer getCode() {
        return state;
    }
}
