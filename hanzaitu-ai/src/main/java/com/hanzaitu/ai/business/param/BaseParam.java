package com.hanzaitu.ai.business.param;

import com.hanzaitu.ai.business.domain.VerifiyImgCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
public class BaseParam extends  VerifiyImgCode{

    @ApiModelProperty("参数")
    @NotBlank(message = "参数不能为空")
    private String param;
}
