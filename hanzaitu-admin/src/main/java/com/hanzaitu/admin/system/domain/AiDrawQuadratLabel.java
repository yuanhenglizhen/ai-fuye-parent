package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_draw_quadrat_label")
public class AiDrawQuadratLabel {

    @TableId
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("sort")
    private Integer sort;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;
}
