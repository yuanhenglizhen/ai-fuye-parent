package com.hanzaitu.ai.business.domain;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@ApiModel("下载版本实体")
@TableName("ai_app_release")
public class AiAppRelease {

    @JsonIgnore
    @TableId("id")
    private Integer id;

    @ApiModelProperty("版本名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("下载地址")
    @TableField("file_url")
    private String download;

    @ApiModelProperty("版本号")
    @TableField("version")
    private double version;

    @ApiModelProperty("创建时间")
    @TableField("create_time")
    private String createTime;
}
