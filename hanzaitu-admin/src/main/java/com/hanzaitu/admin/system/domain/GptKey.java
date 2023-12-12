package com.hanzaitu.admin.system.domain;

import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GptKey {

    @ApiModelProperty("key Id")
    private Integer id;

    @ApiModelProperty("key  内容")
    @NotBlank(message = "key不能为空")
    private String chatKey;

    @ApiModelProperty("GPT 模型")
    @NotNull(message = "模型不能为空")
    private GptKeyTypeEnum model;

    @ApiModelProperty("GPT 代理ip")
    private String proxyHost;

    @ApiModelProperty("GPT 代理端口")
    private String proxyPort;

    @ApiModelProperty("最大token数")
    private Integer maxToken;

    @ApiModelProperty("思维发散值")
    private Double temperature;

    @ApiModelProperty("GPT 创建时间")
    private String createTime;
}
