package com.hanzaitu.common.config;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "kd100.sms")
public class KD100Config {

    @ApiModelProperty("链接地址")
    private String url;
    @ApiModelProperty("密钥")
    private String key;
    @ApiModelProperty("用户id")
    private String userid;
    @ApiModelProperty("售价")
    private String seller;
    @ApiModelProperty("tid")
    private String tid;
}
