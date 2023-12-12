package com.hanzaitu.ai.business.vo;

import com.hanzaitu.ai.business.domain.CustomerUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerUserVO extends CustomerUser {

    @ApiModelProperty("邀请链接")
    private String inviteUrl;

    @ApiModelProperty("个人积分")
    private Integer points;

    @ApiModelProperty("邀请人数")
    private Long inviteCount;
}
