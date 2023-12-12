package com.hanzaitu.admin.system.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_draw_quadrat_label_check")
public class AiDrawQuadratLabelCheck {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("task_id")
    private String taskId;

    @TableField("label_id")
    private Integer labelId;

    /**
     * 原始id
     */
    @TableField("quadrat_id")
    private Long quadratId;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;
}
