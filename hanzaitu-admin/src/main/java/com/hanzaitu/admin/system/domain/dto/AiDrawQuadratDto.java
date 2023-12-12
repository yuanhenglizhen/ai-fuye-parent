package com.hanzaitu.admin.system.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AiDrawQuadratDto {

    @NotNull(message = "用户id不能为空")
    private Long userId;

    @NotNull(message = "图片id不能为空")
    private String taskId;

    @NotNull(message = "提示词不能为空")
    private String prompt;

    @NotNull(message = "图片地址不能为空")
    private String imgUrl;

    @NotNull(message = "审核状态不能为空")
    private String checkStatus;
}
