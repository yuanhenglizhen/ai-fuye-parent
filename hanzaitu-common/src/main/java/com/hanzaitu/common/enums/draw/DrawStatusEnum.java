package com.hanzaitu.common.enums.draw;

import java.util.Objects;

public enum DrawStatusEnum {

    SHOW("0"),
    NOT_SHOW("1");

    private final String code;
    DrawStatusEnum(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public boolean eqValue(Integer value) {
        if (Objects.isNull(value)) {
            return false;
        } else {
            return Objects.isNull(this.code) ? false : value.equals(this.code);
        }
    }
}
