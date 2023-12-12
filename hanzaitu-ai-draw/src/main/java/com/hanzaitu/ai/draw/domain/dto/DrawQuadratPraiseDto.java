package com.hanzaitu.ai.draw.domain.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DrawQuadratPraiseDto {

    @NotBlank(message = "Id不能为空")
    private String taskId;

}
