package com.hanzaitu.ai.business.param;

import com.hanzaitu.ai.business.domain.VerifiyImgCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CustomerUserSaveMailParam extends VerifiyImgCode {


    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("邀请码")
    private String inviteCode;

    @ApiModelProperty("验证码")
    @NotBlank(message = "验证码不能为空")
    private String verificationCode;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("头像")
    private String avatar;
}
