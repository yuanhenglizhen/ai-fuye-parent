package com.hanzaitu.ai.business.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BaseResetParam {
    @ApiModelProperty("参数")
    @NotBlank(message = "参数不能为空")
    private String param;
}
