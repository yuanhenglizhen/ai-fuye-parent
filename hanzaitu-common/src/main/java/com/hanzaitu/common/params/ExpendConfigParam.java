package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 消耗配置
 */
@Data
public class ExpendConfigParam implements Serializable {

    @ApiModelProperty("消耗类型")
    private String expendType;

    @ApiModelProperty("积分")
    private Integer points;
}
