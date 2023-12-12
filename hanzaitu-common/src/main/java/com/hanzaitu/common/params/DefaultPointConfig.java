package com.hanzaitu.common.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 默认积分配置
 */
@Data
public class DefaultPointConfig {

    @ApiModelProperty("初始化积分")
    private Integer initPoints;

    @ApiModelProperty("邀请积分")
    private Integer invitePoints;
}
