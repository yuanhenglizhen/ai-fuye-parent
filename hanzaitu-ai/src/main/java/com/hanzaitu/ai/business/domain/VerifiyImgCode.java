package com.hanzaitu.ai.business.domain;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class VerifiyImgCode {

    @NotNull(message = "验证码信息不能为空")
    private Integer removing;

    @NotBlank(message = "验证码信息不能为空")
    private String chenckMoveid;
}
