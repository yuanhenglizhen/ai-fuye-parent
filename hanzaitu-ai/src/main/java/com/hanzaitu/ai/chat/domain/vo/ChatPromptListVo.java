package com.hanzaitu.ai.chat.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChatPromptListVo {

    /**
     * 分类Id
     */
    private Integer categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 提示词列表
     */
    private List<ChatPrompContentVo> promptList;
}
