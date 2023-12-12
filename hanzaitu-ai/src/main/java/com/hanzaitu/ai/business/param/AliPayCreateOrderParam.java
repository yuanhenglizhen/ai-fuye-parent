package com.hanzaitu.ai.business.param;

import com.hanzaitu.common.params.AliPayConfigParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AliPayCreateOrderParam extends AliPayConfigParam {

    @ApiModelProperty("充值金额")
    private BigDecimal amount;
}
