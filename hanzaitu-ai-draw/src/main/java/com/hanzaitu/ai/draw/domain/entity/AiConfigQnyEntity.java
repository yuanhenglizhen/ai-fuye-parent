package com.hanzaitu.ai.draw.domain.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_config_qny")
public class AiConfigQnyEntity {

    @TableId("id")
    private Integer id;

    @TableField("qny_access_key")
    private String qnyAccessKey;

    @TableField("qny_secret_key")
    private String qnySecretKey;

    @TableField("qny_bucket_name")
    private String qnyBucketName;

    @TableField("qny_image_domain")
    private String qnyImageDomain;

    @TableField("zone")
    private String zone;
}
