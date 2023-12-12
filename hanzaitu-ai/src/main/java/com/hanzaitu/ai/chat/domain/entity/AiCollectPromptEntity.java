package com.hanzaitu.ai.chat.domain.entity;

import lombok.Data;

@Data
public class AiCollectPromptEntity {

    private Integer id;

    private Integer userId;

    private String msgId;

    private String prompt;

    private String winId;

    private String createTime;
}
