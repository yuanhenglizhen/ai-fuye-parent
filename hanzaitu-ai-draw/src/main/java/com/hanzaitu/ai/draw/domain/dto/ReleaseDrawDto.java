package com.hanzaitu.ai.draw.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("提交发布绘画")
public class ReleaseDrawDto {

    /**
     * 自定义参数.
     */
    @NotBlank(message = "任务Id不能为空")
    @ApiModelProperty(value = "任务Id")
    private String taskId;
}
