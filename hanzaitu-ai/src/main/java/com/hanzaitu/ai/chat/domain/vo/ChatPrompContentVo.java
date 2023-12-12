package com.hanzaitu.ai.chat.domain.vo;

import lombok.Data;

@Data
public class ChatPrompContentVo {

    /**
     * 提示词Id
     */
    private Integer prompId;

    /**
     * 提示词标题
     */
    private String title;

    /**
     * 提示词内容
     */
    private String content;
}
