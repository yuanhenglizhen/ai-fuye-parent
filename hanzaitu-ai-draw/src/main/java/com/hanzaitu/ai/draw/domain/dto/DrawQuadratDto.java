package com.hanzaitu.ai.draw.domain.dto;

import com.hanzaitu.common.enums.draw.DrawQuadratSortTypeEnum;
import com.hanzaitu.common.page.PageParam;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DrawQuadratDto extends PageParam {

    /**
     * 标签id
     */
    @NotNull(message = "标签Id不能为空")
    private Integer labelId;

    /**
     * 排序类型
     */
    @NotNull(message = "排序类别不能为空")
    private DrawQuadratSortTypeEnum sortType;


}
