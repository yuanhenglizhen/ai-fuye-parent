package com.hanzaitu.ai.business.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 微信支付回调参数
 */
@Data
public class WxPayCallbackParam {

    @ApiModelProperty("微信支付订单号")
    private String transaction_id;

    @ApiModelProperty("微信支付订单号")
    private String mchid;

    @ApiModelProperty("交易状态")
    private String trade_state;

    @ApiModelProperty("应用ID")
    private String bank_type;

    @ApiModelProperty("支付完成时间")
    private Date success_time;

    @ApiModelProperty("微信支付订单号")
    private String out_trade_no;

    @ApiModelProperty("应用ID")
    private String appid;

    @ApiModelProperty("交易状态描述")
    private String trade_state_desc;

    @ApiModelProperty("交易类型")
    private String trade_type;

    @ApiModelProperty("附加数据")
    private String attach;

    @ApiModelProperty("附加数据")
    private BigDecimal totalAmount;
}
