package com.hanzaitu.ai.chat.domain.entity;

import lombok.Data;

@Data
public class AiPromptCategoryEntity {

    private Integer id;

    private String categoryName;

    private String createBy;

    private String createTime;

    private String updateBy;

    private String remark;

    private String delFlag;
}
