package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 支付宝app支付配置参数
 */
@Data
public class AliPayAppConfigParam {

    @ApiModelProperty(value = "应用id", hidden = true)
    private String appId;

    @ApiModelProperty(value = "私钥", hidden = true)
    private String privateKey;

    @ApiModelProperty(value = "公钥", hidden = true)
    private String alipayPublicKey;

    @ApiModelProperty(value = "回调地址", hidden = true)
    private String notifyUrl;

}
