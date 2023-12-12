package com.hanzaitu.ai.draw.domain.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AiDrawPromptVo {

    private String category;


    private List<AiDrawPromptCategoryVo> classifyList;

}
