package com.hanzaitu.ai.draw.config;


import com.qiniu.common.Zone;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
//@Component
//@ConfigurationProperties(prefix = "upload")
public class UploadLoadConfig {

    private String tmpDir;

    private String qnyAccessKey;

    private String qnySecretKey;

    private String qnyBucketName;

    private String qnyImageDomain;

    private Zone zone;
}
