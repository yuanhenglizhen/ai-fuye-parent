package com.hanzaitu.ai.draw.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hanzaitu.ai.draw.domain.entity.DrawSquareInfoEntity;
import com.hanzaitu.common.config.PhoneJsonSerializer;
import lombok.Data;

@Data
public class AiDrawRraiseListVo extends DrawSquareInfoEntity {

    private String taskId;

    private String prompt;

    private String imgUrl;

    private Double imgWidth;

    private Double imgHeight;

    private String createTime;

    @JsonSerialize(using = PhoneJsonSerializer.class)
    private String userName;
}
