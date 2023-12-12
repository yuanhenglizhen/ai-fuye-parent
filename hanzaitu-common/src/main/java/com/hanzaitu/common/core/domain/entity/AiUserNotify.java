package com.hanzaitu.common.core.domain.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanzaitu.common.enums.NotifyType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("ai_user_notify")
public class AiUserNotify {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("内容")
    @TableField("content")
    private String content;

    @ApiModelProperty("用户Id")
    @TableField("user_id")
    private Long userId;

    @ApiModelProperty("0 正常  1 删除")
    @TableField("del_flag")
    private char delFlag;

    @ApiModelProperty("类型")
    @TableField("type")
    private NotifyType type;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private String createTime;


    @ApiModelProperty("0 未读  1  已读")
    @TableField("read_flag")
    private char readFlag;

}
