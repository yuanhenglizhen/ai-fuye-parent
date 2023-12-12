package com.hanzaitu.common.domain;


import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("首页配置")
@Data
public class HomeViewConfig {

    /**
     * 首页图标
     */
    @NotBlank(message = "首页logo不能为空")
    private String logo;

    /**
     * 介绍文本
     */
    @NotBlank(message = "文案不能为空")
    private String text;


}
