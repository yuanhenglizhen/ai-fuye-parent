package com.hanzaitu.ai.business.param;

import com.hanzaitu.ai.business.domain.VerifiyImgCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户登录入参
 * @author chenrui
 * @date 2022-03-30
 */
@ApiModel(description = "用户登录入参")
@Data
public class LoginParam extends VerifiyImgCode {

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
}
