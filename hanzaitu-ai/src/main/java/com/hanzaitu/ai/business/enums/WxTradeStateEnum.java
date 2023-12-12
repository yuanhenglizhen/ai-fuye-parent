package com.hanzaitu.ai.business.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Objects;

public enum WxTradeStateEnum implements IEnum {

    SUCCESS("SUCCESS", "支付成功"),
    REFUND("REFUND","转入退款"),
    NOTPAY("NOTPAY", "未支付"),
    CLOSED("CLOSED", "已关闭"),
    REVOKED("REVOKED", "已撤销"),
    USERPAYING("USERPAYING", "用户支付中"),
    PAYERROR("PAYERROR", "支付失败");

    private String value;
    private String description;

    private WxTradeStateEnum(String value, String description) {
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
