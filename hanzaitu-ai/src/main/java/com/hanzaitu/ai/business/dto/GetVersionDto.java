package com.hanzaitu.ai.business.dto;

import com.hanzaitu.common.enums.AppTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel(value = "版本查询对象")
@Data
public class GetVersionDto {

    @NotNull(message = "版本不能null 且要保留一位小数")
    @ApiModelProperty(value = "版本，double 保留一位小数点",dataType = "double")
    private Double version;

    @ApiModelProperty(value = "安装包类型")
    @NotNull(message = "app类型不能null")
    private AppTypeEnum appType;

}
