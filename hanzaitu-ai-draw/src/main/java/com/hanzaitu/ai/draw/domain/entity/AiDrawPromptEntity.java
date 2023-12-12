package com.hanzaitu.ai.draw.domain.entity;


import lombok.Data;

@Data
public class AiDrawPromptEntity {

    private Integer id;

    /**
     * 指令
     */
    private String direct;

    /**
     * 描述
     */
    private String depict;

    /**
     * 分类
     */
    private String classify;

    /**
     * 大类
     */
    private String category;
}
