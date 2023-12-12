package com.hanzaitu.ai.business.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Objects;

public enum HandleStatusEnum implements IEnum {


    DONE("done", "处理完毕"),
    WAIT("wait","待处理"),
    ERROR("error", "处理失败"),
    TIMEOUT("timeout", "超时");

    private String value;
    private String description;

    private HandleStatusEnum(String value, String description) {
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
}
