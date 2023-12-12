package com.hanzaitu.ai.draw.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;


@Data
@ApiModel("上传图片")
public class UploadImageDto {

    @NotBlank(message = "文件不能为空")
    @ApiModelProperty(value = "文件字段")
    private MultipartFile file;

}
