package com.hanzaitu.ai.chat.domain.vo;

import lombok.Data;

@Data
public class ChatHistoryTitleVo {

    /**
     * 窗口id
     */
    private String winId;

    /**
     * 窗口标题
     */
    private String winTitle;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 子模型
     */
    private String subModel;
}
