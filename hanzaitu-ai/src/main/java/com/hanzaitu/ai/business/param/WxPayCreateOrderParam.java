package com.hanzaitu.ai.business.param;

import com.hanzaitu.common.params.WxPayConfigParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建微信支付订单
 */
@Data
public class WxPayCreateOrderParam extends WxPayConfigParam {

    @ApiModelProperty("充值金额")
    private BigDecimal amount;

}
