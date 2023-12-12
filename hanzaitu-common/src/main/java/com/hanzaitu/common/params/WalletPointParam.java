package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WalletPointParam {

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("积分")
    private Integer points;
}
