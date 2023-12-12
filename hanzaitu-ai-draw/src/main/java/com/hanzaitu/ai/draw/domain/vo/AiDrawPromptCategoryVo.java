package com.hanzaitu.ai.draw.domain.vo;


import lombok.Data;

import java.util.List;

@Data
public class AiDrawPromptCategoryVo {

    private String categoryName;

    private List<AiDrawPromptContentVo> categoryContent;
}
