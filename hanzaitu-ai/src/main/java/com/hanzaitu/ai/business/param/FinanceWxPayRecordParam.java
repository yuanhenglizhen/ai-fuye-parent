package com.hanzaitu.ai.business.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 微信支付记录参数
 */
@Data
public class FinanceWxPayRecordParam {

    private Long id;

    @ApiModelProperty("用户id")
    private Long customerUserId;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("商户订单号")
    private String outTradeNo;

    @ApiModelProperty("商户订单号")
    private String transactionId;

    @ApiModelProperty("交易状态")
    private String tradeState;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("支付二维码")
    private String codeUrl;
}
