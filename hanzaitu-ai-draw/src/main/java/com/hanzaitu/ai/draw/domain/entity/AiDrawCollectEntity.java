package com.hanzaitu.ai.draw.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hanzaitu.common.config.PhoneJsonSerializer;
import lombok.Data;

@Data
@TableName("ai_draw_collect")
public class AiDrawCollectEntity extends DrawSquareInfoEntity {

    @JsonIgnore
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private String taskId;

    @TableField("prompt")
    private String prompt;

    @TableField("img_url")
    private String imgUrl;

    @JsonIgnore
    @TableField("user_id")
    private Long userId;

    @JsonIgnore
    @TableField("status")
    private String status;

    @TableField("create_time")
    private String createTime;

    @JsonIgnore
    @TableField("update_time")
    private String updateTime;

    @TableField("img_width")
    private Double imgWidth;

    @TableField("img_height")
    private Double imgHeight;

    /**
     * 用户名
     */
    @JsonSerialize(using = PhoneJsonSerializer.class)
    @TableField("user_name")
    private String userName;
}
