package com.hanzaitu.ai.draw.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@TableName("ai_draw_quadrat_label")
@Data
public class AiDrawQuadratLabelEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("sort")
    private Integer sort;

    @JsonIgnore
    @TableField("create_time")
    private String createTime;

    @JsonIgnore
    @TableField("update_time")
    private String updateTime;

}
