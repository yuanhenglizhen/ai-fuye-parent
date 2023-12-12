package com.hanzaitu.ai.business.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class WxPayVO {

    @ApiModelProperty("创建微信订单返回的二维码")
    private String msg;

    @ApiModelProperty("创建的订单号")
    private String resultNo;
}
