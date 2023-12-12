package com.hanzaitu.ai.business.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 支付宝回调参数
 */
@Data
public class AliPayCallBackParam implements Serializable {

    @ApiModelProperty("支付宝交易号")
    private String trade_no;

    @ApiModelProperty("支付宝应用的APPID")
    private String app_id;

    @ApiModelProperty("商家订单号")
    private String out_trade_no;

    @ApiModelProperty("商家业务号")
    private String out_biz_no;

    @ApiModelProperty("买家支付宝账号 ID")
    private String buyer_id;

    @ApiModelProperty("卖家支付宝账号 ID")
    private String seller_id;

    @ApiModelProperty("交易状态")
    private String trade_status;

    @ApiModelProperty("订单金额")
    private String total_amount;

    @ApiModelProperty("实收金额")
    private String receipt_amount;

    @ApiModelProperty("订单标题")
    private String subject;

    @ApiModelProperty("商品描述")
    private String body;

    @ApiModelProperty("交易创建时间")
    private String gmt_create;

    @ApiModelProperty("交易付款时间")
    private String gmt_payment;

    @ApiModelProperty("交易结束时间")
    private String gmt_close;

}
