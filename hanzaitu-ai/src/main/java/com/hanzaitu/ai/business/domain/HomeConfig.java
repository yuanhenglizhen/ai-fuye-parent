package com.hanzaitu.ai.business.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("首页配置")
@Data
public class HomeConfig {

    @ApiModelProperty("首页Logo")
    private String logo;

    @ApiModelProperty("文字介绍")
    private String text;

    @ApiModelProperty("登录框图片")
    private String loginExhibit;

    @ApiModelProperty("登录注册方式")
    private LoginRegist loginRegist;

}

@Data
class LoginRegist {

    @ApiModelProperty("手机号码注册/登录")
    private Integer phone;

    @ApiModelProperty("邮箱注册/登录")
    private Integer email;
}
