package com.hanzaitu.ai.business.param;

import com.hanzaitu.common.params.YPayConfigParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class YPayCreateOrderParam extends YPayConfigParam {

    @ApiModelProperty("充值金额")
    private BigDecimal amount;

    @ApiModelProperty("付款方式")
    private String payType;
}
