package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class YPayConfigVO {

    @ApiModelProperty(value = "商户id")
    private String merchantId;

    @ApiModelProperty(value = "页面跳转通知地址")
    private String returnUrl;

    @ApiModelProperty(value = "回调地址")
    private String notifyUrl;

    @ApiModelProperty(value = "请求地址")
    private String postUrl;

    @ApiModelProperty(value = "web请求地址")
    private String postWebUrl;

    @ApiModelProperty(value = "密钥")
    private String token;

    @ApiModelProperty(value = "重定向地址")
    private String redirectUrl;
}
