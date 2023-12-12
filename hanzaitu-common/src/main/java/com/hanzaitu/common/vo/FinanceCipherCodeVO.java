package com.hanzaitu.common.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 卡密codeVo
 */
@Data
public class FinanceCipherCodeVO {

    private Long id;

    @ApiModelProperty("卡密金额")
    private String totalAmount;

    @ApiModelProperty("卡密积分")
    private Integer points;

    @ApiModelProperty("卡密")
    private String cipherCode;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("状态")
    private String statusName;


    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

}
