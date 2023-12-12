package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付积分配置
 */
@Data
public class PayToPointConfigVO {

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("积分")
    private Integer points;
}
