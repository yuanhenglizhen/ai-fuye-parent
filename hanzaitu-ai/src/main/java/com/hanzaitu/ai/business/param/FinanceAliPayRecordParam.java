package com.hanzaitu.ai.business.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 */
@Data
public class FinanceAliPayRecordParam {

    private Long id;

    @ApiModelProperty("用户id")
    private Long customerUserId;

    @ApiModelProperty("订单金额")
    private BigDecimal totalAmount;

    @ApiModelProperty("实收金额")
    private BigDecimal receiptAmount;

    @ApiModelProperty("商家订单号")
    private String outTradeNo;

    @ApiModelProperty("商户订单号")
    private String tradeNo;

    @ApiModelProperty("买家id")
    @TableField("buyer_id")
    private String buyerId;

    @ApiModelProperty("卖家id")
    private String sellerId;

    @ApiModelProperty("交易状态")
    private String tradeStatus;

    @ApiModelProperty("付款时间")
    private String gmtPayment;

    @ApiModelProperty("处理状态")
    private String handleStatus;

    @ApiModelProperty("返回类容")
    private String body;
}
