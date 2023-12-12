package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;


@ApiModel("七牛云配置")
@Data
@TableName("sys_config_qny")
public class SysQnyConfig {

    @TableId(value="id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("AccessKey")
    @NotBlank(message = "qnyAccessKey 不能为空")
    @TableField("qny_access_key")
    private String qnyAccessKey;

    @ApiModelProperty("qnySecretKey")
    @NotBlank(message = "qnySecretKey 不能为空")
    @TableField("qny_secret_key")
    private String qnySecretKey;

    @ApiModelProperty("qnyBucketName")
    @NotBlank(message = "qnyBucketName 不能为空")
    @TableField("qny_bucket_name")
    private String qnyBucketName;

    @ApiModelProperty("域名")
    @NotBlank(message = "域名 不能为空")
    @TableField("qny_image_domain")
    private String qnyImageDomain;
}
