package com.hanzaitu.ai.business.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

import java.util.Objects;

public enum AliPayTradeStatusEnum implements IEnum {


    WAIT_BUYER_PAY("WAIT_BUYER_PAY", "交易创建，等待买家付款"),
    TRADE_CLOSED("TRADE_CLOSED","未付款交易超时关闭，或支付完成后全额退款"),
    TRADE_SUCCESS("TRADE_SUCCESS", "交易支付成功"),
    TRADE_FINISHED("TRADE_FINISHED", "交易结束，不可退款");

    private String value;
    private String description;

    private AliPayTradeStatusEnum(String value, String description) {
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
