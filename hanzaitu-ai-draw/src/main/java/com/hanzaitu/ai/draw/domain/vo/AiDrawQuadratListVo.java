package com.hanzaitu.ai.draw.domain.vo;

import lombok.Data;

@Data
public class AiDrawQuadratListVo {

    private String id;

    private Boolean square;

    private String imgUrl;

    private String prompt;

    private Integer squareNum;
}
