package com.hanzaitu.ai.business.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class YPayCallBackParam {

    @ApiModelProperty("源支付交易号")
    private String trade_no;

    @ApiModelProperty("交易号")
    private String out_trade_no;

    @ApiModelProperty("支付类型")
    private String type;

    @ApiModelProperty("金额")
    private BigDecimal money;

    @ApiModelProperty("状态")
    private String trade_status;


}
