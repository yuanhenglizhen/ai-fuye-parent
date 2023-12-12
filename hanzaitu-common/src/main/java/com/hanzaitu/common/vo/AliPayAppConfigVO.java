package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 支付宝app支付配置VO
 */
@Data
public class AliPayAppConfigVO {


    @ApiModelProperty("应用id")
    private String appId;

    @ApiModelProperty("私钥")
    private String privateKey;

    @ApiModelProperty("公钥")
    private String alipayPublicKey;

    @ApiModelProperty("回调地址")
    private String notifyUrl;
}
