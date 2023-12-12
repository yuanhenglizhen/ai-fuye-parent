package com.hanzaitu.common.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Objects;

public enum ValidStatusEnum  implements IEnum {

    INVALID(0, "无效"),
    VALID(1, "有效");

    private Integer value;
    private String description;

    private ValidStatusEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean eqValue(Integer value) {
        if (Objects.isNull(value)) {
            return false;
        } else {
            return Objects.isNull(this.value) ? false : value.equals(this.value);
        }
    }
}
