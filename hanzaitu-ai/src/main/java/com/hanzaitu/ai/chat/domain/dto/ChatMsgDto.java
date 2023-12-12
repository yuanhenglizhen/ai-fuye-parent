package com.hanzaitu.ai.chat.domain.dto;


import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ChatMsgDto {

    @NotBlank(message = "窗口Id不能为空")
    private String winId;

    @NotBlank(message = "消息Id不能为空")
    private String msgId;

    @NotBlank(message = "模型不能为空")
    private String model;

    @NotBlank(message = "聊天内容不能为空")
    private String prompt;
}
