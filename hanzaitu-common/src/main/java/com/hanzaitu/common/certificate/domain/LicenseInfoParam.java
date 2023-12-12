package com.hanzaitu.common.certificate.domain;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class LicenseInfoParam {

    @NotBlank(message = "key不能为空")
    private String key;


    @NotBlank(message = "内容不能为空")
    private String content;
}
