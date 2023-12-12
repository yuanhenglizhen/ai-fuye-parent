package com.hanzaitu.common.domain;


import com.hanzaitu.common.enums.GptKeyTypeEnum;
import com.plexpt.chatgpt.entity.chat.ChatCompletion;
import lombok.Data;

@Data
public class ChatKeyEntity {

    private Integer id;

    private String chatKey;

    private Integer useNumber;

    private GptKeyTypeEnum model;

    private Integer maxToken;

    private Double temperature;

    private String proxyHost;

    private String proxyPort;

    private String createTime;

    /**
     * 上下文数量
     */
    private Integer numberOfContexts;
}
