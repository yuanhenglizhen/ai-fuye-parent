package com.hanzaitu.common.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanzaitu.common.utils.DateUtils;
import lombok.Data;

/**
 * 绘画广场审核表
 */
@Data
@TableName("ai_draw_quadrat_check")
public class AiDrawQuadratCheckEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

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
    private Integer checkUserId;


    @TableField("user_id")
    private Integer userId;


    /*@TableField("img_width")
    private Double imgWidth;

    @TableField("img_height")
    private Double imgHeight;*/
}
