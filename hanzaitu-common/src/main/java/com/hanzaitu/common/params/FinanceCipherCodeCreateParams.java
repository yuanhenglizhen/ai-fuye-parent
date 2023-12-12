package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建卡密
 * @author cr
 */
@Data
public class FinanceCipherCodeCreateParams {

    @ApiModelProperty("数量")
    private Integer count;

    @ApiModelProperty("金额")
    private BigDecimal amount;

    @ApiModelProperty("积分")
    private Integer points;
}
