package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class FinanceUserWalletRecordSaveParam {

    @ApiModelProperty("使用用户")
    private Long customerUserId;

    @ApiModelProperty("积分")
    private Integer points;

    @ApiModelProperty("记录类型")
    private String recordType;

}
