package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AliPayConfigVO {

    @ApiModelProperty("应用id")
    private String appId;

    @ApiModelProperty("私钥")
    private String privateKey;

    @ApiModelProperty("公钥")
    private String alipayPublicKey;

    @ApiModelProperty("回调地址")
    private String notifyUrl;

    @ApiModelProperty(value = "返回地址")
    private String returnUrl;
}
