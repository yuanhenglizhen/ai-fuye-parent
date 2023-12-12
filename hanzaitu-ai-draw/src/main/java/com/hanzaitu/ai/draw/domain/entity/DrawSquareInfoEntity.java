package com.hanzaitu.ai.draw.domain.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class DrawSquareInfoEntity {

    /**
     * 点赞数
     */
    @TableField(value = "wie_num",exist = false)
    private Integer wieNum;

    /**
     * 是否被收藏
     */
    @TableField(value = "square_status",exist = false)
    private Boolean squareStatus;
}
