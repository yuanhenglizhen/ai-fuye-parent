package com.hanzaitu.admin.system.domain.dto;


import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.models.auth.In;
import lombok.Data;
import org.springframework.security.web.PortResolverImpl;

import javax.validation.constraints.NotNull;

@Data
public class DrawCheckDto {

    @NotNull(message = "id不能为空")
    private String taskId;

    @NotNull(message = "审核状态不能为空")
    private Integer checkStatus;

    @NotNull(message = "分类Id不能为空")
    private String labelId;

    private String remark;
}
