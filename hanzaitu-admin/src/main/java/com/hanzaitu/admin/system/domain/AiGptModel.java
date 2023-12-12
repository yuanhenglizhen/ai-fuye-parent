package com.hanzaitu.admin.system.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@ApiModel("模型配置")
@TableName("ai_gpt_model")
public class AiGptModel {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("模型")
    @NotBlank(message = "模型不能为空")
    @TableField("model")
    private String model;

    @NotBlank(message = "模型不能为空")
    @TableField("is_on")
    private String isOn;

    @NotBlank(message = "模型不能为空")
    @TableField("type")
    private String type;

}
