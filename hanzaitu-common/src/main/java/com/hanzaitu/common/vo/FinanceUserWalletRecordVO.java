package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FinanceUserWalletRecordVO {


    private Long id;

    @ApiModelProperty("用户id")
    private Long customerUserId;

    @ApiModelProperty("积分")
    private Integer points;

    @ApiModelProperty("变更类型")
    private String recordType;

    @ApiModelProperty("变更类型")
    private String recordTypeName;

    @ApiModelProperty("充值时间")
    private LocalDateTime createTime;
}
