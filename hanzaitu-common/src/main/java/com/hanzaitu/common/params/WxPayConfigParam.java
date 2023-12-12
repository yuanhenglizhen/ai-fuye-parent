package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 微信支付配置保存参数
 */
@Data
public class WxPayConfigParam {

    @ApiModelProperty(value = "商家号")
    private String merchantId;

    @ApiModelProperty(value = "私钥证书地址")
    private String privateKeyPath;

    @ApiModelProperty(value = "证书序列号")
    private String merchantSerialNumber;

    @ApiModelProperty(value = "api密钥")
    private String apiV3key;

    @ApiModelProperty(value = "appId")
    private String appId;

    @ApiModelProperty(value = "回调地址")
    private String notifyUrl;
}
