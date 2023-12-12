package com.hanzaitu.ai.chat.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@ApiModel("收藏提示词")
@Data
public class ChatCollectPromptDto {

    @NotNull(message = "操作信息不能为空")
    @ApiModelProperty("操作信息：add为添加收藏  del为取消收藏")
    private String operate;

    @NotBlank(message = "消息Id")
    @ApiModelProperty("消息Id")
    private String msgId;

    //@NotBlank(message = "窗口Id不能为空")
    @ApiModelProperty("聊天内容")
    private String content;

    @Getter
    @AllArgsConstructor
    public enum ChatCollectOperate {
        ADD("add"),
        DEL("del"),;
        private String value;
    }

}
