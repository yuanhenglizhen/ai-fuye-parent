package com.hanzaitu.ai.business.param;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hanzaitu.ai.business.domain.VerifiyImgCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 注册时保存方法
 */
@Data
public class CustomerUserSaveParam extends VerifiyImgCode {

    @ApiModelProperty("用户名")
    private String userName;

    @Length(max = 12,message = "昵称最多12个字符")
    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("手机号")
    private String phoneNumber;

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
