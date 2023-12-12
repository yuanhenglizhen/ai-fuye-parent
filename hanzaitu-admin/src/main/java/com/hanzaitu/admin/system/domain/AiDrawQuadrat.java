package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//已上架的图-表
@Data
@TableName("ai_draw_quadrat")
public class AiDrawQuadrat {

    @TableId
    private Integer id;

    @TableField("user_id")
    private Long userId;

    @TableField("task_id")
    private String taskId;

    @TableField("prompt")
    private String prompt;

    @TableField("img_url")
    private String imgUrl;

    @TableField("wie_num")
    private Long wieNum;

    @TableField("status")
    private String status;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;

    @TableField("username")
    private String username;

    @TableField("img_width")
    private Double imgWidth;

    @TableField("img_height")
    private Double imgHeight;

}
