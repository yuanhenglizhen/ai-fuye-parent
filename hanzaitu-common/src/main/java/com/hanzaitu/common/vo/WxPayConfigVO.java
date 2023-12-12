package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信支付配置VO
 */
@Data
public class WxPayConfigVO {

    @ApiModelProperty("商家号")
    private String merchantId;

    @ApiModelProperty("私钥证书地址")
    private String privateKeyPath;

    @ApiModelProperty("证书序列号")
    private String merchantSerialNumber;

    @ApiModelProperty("api密钥")
    private String apiV3key;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("回调地址")
    private String notifyUrl;
}
