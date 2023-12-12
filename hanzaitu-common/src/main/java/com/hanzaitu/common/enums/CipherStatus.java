package com.hanzaitu.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Objects;

/**
 * 卡密状态枚举
 */
public enum CipherStatus implements IEnum {

    INVALID("invalid", "失效"),
    USED("used", "已使用"),
    USABLE("usable", "可用");

    private String value;
    private String description;

    private CipherStatus(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean eqValue(String value) {
        if (Objects.isNull(value)) {
            return false;
        } else {
            return Objects.isNull(this.value) ? false : value.equals(this.value);
        }
    }

    public static String getDescriptionByValue(String value) {
        CipherStatus[] values = CipherStatus.values();
        for (CipherStatus status: values) {
            if (value.equals(status.value)) {
                return status.description;
            }
        }
        return null;
    }

}
