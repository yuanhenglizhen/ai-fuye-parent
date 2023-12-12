package com.hanzaitu.common.domain;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
public class PayTypeConfig {

    @NotNull(message = "微信支付配置不能为null")
    private Integer wx;

    @NotNull(message = "支付宝配置不能为null")
    private Integer zfb;

    @NotNull(message = "源支付配置不能为null")
    private Integer yzf;
}
