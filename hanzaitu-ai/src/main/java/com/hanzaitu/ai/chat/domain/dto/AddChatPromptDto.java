package com.hanzaitu.ai.chat.domain.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@ApiModel("添加自定义提示词")
@Data
public class AddChatPromptDto {

    @NotBlank(message = "标题信息不能为空")
    @ApiModelProperty("标题")
    @Length(min = 1,max = 80,message = "标题不能超过80个字符")
    private String title;


    @NotBlank(message = "内容不能为空")
    @ApiModelProperty("内容")
    @Length(min = 1,max = 400,message = "内容不能超过400个字符")
    private String content;
}
