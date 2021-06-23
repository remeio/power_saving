package com.why.powerlistener.domain;

public enum MessageType {
    POWER_TASK(1),
    ;
    private final int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
