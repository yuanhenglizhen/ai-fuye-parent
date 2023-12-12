package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AliPayConfigParam {

    @ApiModelProperty(value = "应用id")
    private String appId;

    @ApiModelProperty(value = "私钥")
    private String privateKey;

    @ApiModelProperty(value = "公钥")
    private String alipayPublicKey;

    @ApiModelProperty(value = "回调地址")
    private String notifyUrl;

    @ApiModelProperty(value = "返回地址")
    private String returnUrl;
}
