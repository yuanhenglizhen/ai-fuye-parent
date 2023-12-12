package com.hanzaitu.ai.business.param;

import com.hanzaitu.ai.business.domain.VerifiyImgCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 用户登录入参
 * @author chenrui
 * @date 2022-03-30
 */
@ApiModel(description = "用户登录入参")
@Data
public class LoginPhoneParam extends VerifiyImgCode {

    @ApiModelProperty(value = "手机号码", required = true)
    @NotBlank(message = "手机号码不能为空")
    private String phoneNumber;

    @NotBlank(message = "验证码码不能为空")
    public String verificationCode;
}
