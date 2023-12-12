package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_draw_record")
public class AiDrawRecordEntity {


    @TableField("task_id")
    private String taskId;

    @TableField("user_id")
    private Long userId;

    @TableField("prompt")
    private String prompt;

    @TableField("prompt_en")
    private String promptEn;

    @TableField("picture")
    private String picture;


    @TableField("img_width")
    private Double imgWidth;

    @TableField("img_height")
    private Double imgHeight;
}
