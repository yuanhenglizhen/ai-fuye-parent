package com.hanzaitu.admin.system.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel("系统通知实体")
@TableName("ai_sys_notify")
public class AiSysNotify {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    @NotBlank(message = "标题不能为空")
    @TableField("title")
    private String title;

    @ApiModelProperty("内容")
    @NotBlank(message = "内容不能为空")
    @TableField("content")
    private String content;

    @ApiModelProperty("通知开始时间")
    @NotBlank(message = "开始时间不能为空")
    @TableField("start_time")
    private String startTime;

    @ApiModelProperty("通知结束时间")
    @NotBlank(message = "结束时间不能为空")
    @TableField("end_time")
    private String endTime;

    @ApiModelProperty("是否启用 0 启用  1 禁用")
    @NotNull(message = "启用标志不能为空")
    @TableField("del_flag")
    private Integer delFlag;
}
