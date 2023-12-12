package com.hanzaitu.admin.system.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hanzaitu.common.enums.NotifyType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("用户通知实体")
@TableName("ai_user_notify")
public class AiUserNotify {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    @NotBlank(message = "标题不能为空")
    @TableField(value = "title")
    private String title;

    @ApiModelProperty("内容")
    @NotBlank(message = "内容不能为空")
    @TableField(value = "content")
    private String content;

    @JsonIgnore
    @TableField(value = "type")
    private NotifyType type;


    @TableField(value = "create_time")
    private String createTime;

}
