package com.hanzaitu.common.enums.draw;

import java.util.Objects;

/**
 * 审核状态枚举
 */
public enum CheckStatusEnum {


    WAIT_CHECK("0", "待审核"),
    CHECK_COMPLETED("1", "审核完成"),
    REJECTED("2", "已驳回");

    private final String code;
    private final String info;

    CheckStatusEnum(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }

    public boolean eqValue(Integer value) {
        if (Objects.isNull(value)) {
            return false;
        } else {
            return Objects.isNull(this.code) ? false : value.equals(this.code);
        }
    }

}
