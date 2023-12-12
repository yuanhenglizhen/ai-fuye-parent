package com.hanzaitu.common.domain;

    import com.baomidou.mybatisplus.annotation.FieldFill;
    import com.baomidou.mybatisplus.annotation.FieldStrategy;
    import com.baomidou.mybatisplus.annotation.TableField;
    import com.baomidou.mybatisplus.annotation.TableName;
    import java.io.Serializable;
    import java.math.BigDecimal;
    import java.time.LocalDateTime;
    import io.swagger.annotations.ApiModel;
    import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;


/**
* <p>
    * 源支付支付记录
    * </p>
*
* @author cr
* @since 2023-06-14
*/
@Getter
@Setter
    @TableName("finance_y_pay_record")
    @ApiModel(value = "FinanceYPayRecord对象", description = "源支付支付记录")
public class FinanceYPayRecord implements Serializable {

private static final long serialVersionUID = 1L;


    private Long id;

            @ApiModelProperty("用户id")
        @TableField("customer_user_id")
    private Long customerUserId;

            @ApiModelProperty("商户id")
        @TableField("merchant_id")
    private Long merchantId;

            @ApiModelProperty("订单金额")
        @TableField("total_amount")
    private BigDecimal totalAmount;

            @ApiModelProperty("实收金额")
        @TableField("receipt_amount")
    private BigDecimal receiptAmount;


    @ApiModelProperty("积分")
    @TableField("points")
    private Integer points;

            @ApiModelProperty("商家订单号")
        @TableField("out_trade_no")
    private String outTradeNo;

            @ApiModelProperty("商户订单号")
        @TableField("trade_no")
    private String tradeNo;

            @ApiModelProperty("支付方式")
        @TableField("type")
    private String type;

            @ApiModelProperty("卖家id")
        @TableField("seller_id")
    private String sellerId;

            @ApiModelProperty("交易状态")
        @TableField("trade_status")
    private String tradeStatus;

            @ApiModelProperty("付款时间")
        @TableField("gmt_payment")
    private String gmtPayment;

            @ApiModelProperty("处理状态")
        @TableField("handle_status")
    private String handleStatus;

            @ApiModelProperty("创建时间")
            @TableField(value = "create_time", fill = FieldFill.INSERT , updateStrategy= FieldStrategy.NEVER)
    private LocalDateTime createTime;

            @ApiModelProperty("更新时间")
            @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE )
    private LocalDateTime updateTime;

}
