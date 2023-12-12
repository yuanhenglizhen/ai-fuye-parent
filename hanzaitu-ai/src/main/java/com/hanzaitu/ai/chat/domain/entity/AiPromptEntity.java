package com.hanzaitu.ai.chat.domain.entity;


import lombok.Data;

@Data
public class AiPromptEntity {

    private Integer id;

    private String prompt;

    private String role;

    private Integer usageCount;

    private Integer categoryId;

    private String createTime;

    private String remark;

    private String delFlag;

}
