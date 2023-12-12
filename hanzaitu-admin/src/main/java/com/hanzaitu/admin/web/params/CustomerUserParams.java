package com.hanzaitu.admin.web.params;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CustomerUserParams {

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("手机号")
    private String phoneNumber;

    @ApiModelProperty("状态")
    private String status;
}
