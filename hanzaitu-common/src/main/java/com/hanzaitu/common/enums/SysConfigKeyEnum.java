package com.hanzaitu.common.enums;


import com.baomidou.mybatisplus.annotation.IEnum;

import java.io.Serializable;
import java.util.Objects;

public enum SysConfigKeyEnum  implements IEnum {

    CONTACT_US("contact_us", "联系我们"),
    WX_PAY("wx_pay", "微信支付配置"),
    ALI_PAY("ali_pay", "支付宝支付配置"),
    ALI_PAY_APP("ali_pay_app", "支付宝app支付配置"),
    Y_PAY("y_pay", "y支付配置"),
    PAY_TO_POINTS("pay_to_points", "支付积分配置"),
    KD100("kd100", "快递100配置"),

    EXPEND_POINTS("expend_points", "积分消耗配置"),
    DEFAULT_POINTS("default_points", "默认积分配置"),
    PAY_WAY("pay_way", "支付方式配置"),

    HOME_CONFIG("home_config", "首页配置"),
    EMAIL_CONFIG("email_config", "邮箱配置"),
    ;

    private String value;
    private String description;

    private SysConfigKeyEnum(String value, String description) {
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

    public boolean eqValue(Integer value) {
        if (Objects.isNull(value)) {
            return false;
        } else {
            return Objects.isNull(this.value) ? false : value.equals(this.value);
        }
    }
}
