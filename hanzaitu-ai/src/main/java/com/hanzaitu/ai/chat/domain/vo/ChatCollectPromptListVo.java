package com.hanzaitu.ai.chat.domain.vo;

import lombok.Data;

@Data
public class ChatCollectPromptListVo {
    /**
     * 收藏Id
     */
    private Integer collectId;


    /**
     * 消息Id
     */
    private String msgId;

    /**
     * 提示词内容
     */
    private String content;

    /**
     * 创建时间
     */
    private String createTime;

}
