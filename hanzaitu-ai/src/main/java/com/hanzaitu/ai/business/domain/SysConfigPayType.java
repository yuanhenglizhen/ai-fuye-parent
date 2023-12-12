package com.hanzaitu.ai.business.domain;


import lombok.Data;

@Data
public class SysConfigPayType {

    /**
     * 微信支付
     */
    private Integer wx;

    /**
     * 支付宝支付
     */
    private Integer zfb;

    /**
     * 源支付
     */
    private Integer yzf;
}
