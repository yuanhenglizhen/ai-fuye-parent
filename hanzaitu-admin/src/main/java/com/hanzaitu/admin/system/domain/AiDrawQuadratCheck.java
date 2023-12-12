package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

//审核表
@Data
@TableName("ai_draw_quadrat_check")
public class AiDrawQuadratCheck {

    @TableId
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("task_id")
    private String taskId;

    @TableField("create_time")
    private String createTime;

    @TableField("update_time")
    private String updateTime;

    @TableField("check_time")
    private String checkTime;

    @TableField("check_status")
    private String checkStatus;

    @TableField("check_user_id")
    private Long checkUserId;

}
