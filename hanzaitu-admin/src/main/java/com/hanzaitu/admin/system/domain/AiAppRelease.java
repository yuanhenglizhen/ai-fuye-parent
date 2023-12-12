package com.hanzaitu.admin.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hanzaitu.common.enums.AppTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@ApiModel("app发布参数")
@Data
@TableName("ai_app_release")
public class AiAppRelease {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @NotBlank(message = "名称不能为空")
    @ApiModelProperty("发布包名称")
    @TableField("name")
    private String name;

    @NotBlank(message = "地址不能为空")
    @ApiModelProperty("下载地址")
    @TableField("file_url")
    private String fileUrl;

    @NotNull(message = "版本号不能为null")
    @ApiModelProperty("版本号")
    @TableField("version")
    private Double version;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private String createTime;

    @NotNull(message = "安装包类型不能为null")
    @ApiModelProperty("安装包类型")
    @TableField("app_type")
    private AppTypeEnum appType;
}
