package com.hanzaitu.ai.chat.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value="根据窗口Id查询聊天记录")
public class ChatHistoryContentDto {

    @NotBlank(message = "窗口Id不能为空")
    @ApiModelProperty("窗口Id")
    private String winId;

}
