package com.hanzaitu.ai.chat.domain.dto;

import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value="操作聊天窗口")
public class ChatHistoryDto {

    /**
     * choose 选择    clean 全部清空
     */
    @NotBlank(message = "操作指令不能为空")
    @NotNull(message = "操作指令不能为空")
    @ApiModelProperty("choose 选择    clean 全部清空")
    private String operate;

    /**
     * 窗口id  多个用逗号隔开
     */
    @ApiModelProperty("窗口id  多个用逗号隔开")
    private String winId;

    @NotNull(message = "模型不能为空")
    @ApiModelProperty("请求模型 GPT_3,GPT 4 ")
    private GptKeyTypeEnum model;

}
