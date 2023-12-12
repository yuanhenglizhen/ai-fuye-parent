package com.hanzaitu.ai.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("聊天数据")
@Data
public class ChatParamDto {

    @ApiModelProperty("提问")
    private String question;

    @ApiModelProperty("消息Id")
    private String msgId;

    @ApiModelProperty("窗口Id")
    private String winId;

    @JsonIgnore
    private String model;
}


