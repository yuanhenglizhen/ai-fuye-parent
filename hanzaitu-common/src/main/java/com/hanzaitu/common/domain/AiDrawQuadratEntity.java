package com.hanzaitu.common.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hanzaitu.common.config.PhoneJsonSerializer;
import lombok.Data;

@Data
@TableName("ai_draw_quadrat")
public class AiDrawQuadratEntity {

    @JsonIgnore
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private String taskId;

    @TableField("prompt")
    private String prompt;

    @TableField("img_url")
    private String imgUrl;

    @TableField("wie_num")
    private Integer wieNum;

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

    @JsonSerialize(using = PhoneJsonSerializer.class)
    @TableField("user_name")
    private String userName;

    @TableField("img_width")
    private Double imgWidth;

    @TableField("img_height")
    private Double imgHeight;

    private Boolean squareStatus;
}
