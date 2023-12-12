package com.hanzaitu.common.enums;

public enum SmsEnum {
    VERIFY("8020");
    String tid;

    SmsEnum(String tid) {
        this.tid = tid;
    }


    public String getTid() {
        return tid;
    }
}
