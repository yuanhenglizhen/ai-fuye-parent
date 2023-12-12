package com.hanzaitu.common.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EmailConfig {

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("授权码")
    private String authCode;

    @ApiModelProperty("发送用户名")
    private String sendName;

    @ApiModelProperty("发送内容")
    private String sendContent;

    @ApiModelProperty("标题")
    private String sendTitle;

    @ApiModelProperty("服务商地址")
    private String smtpHost;
}
