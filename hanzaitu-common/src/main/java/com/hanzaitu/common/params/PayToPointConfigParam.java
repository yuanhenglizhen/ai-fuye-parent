package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 支付积分参数
 */
@Data
public class PayToPointConfigParam {

    @ApiModelProperty("金额")
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;

    @ApiModelProperty("积分")
    @NotNull(message = "积分不能为空")
    private Integer points;
}
