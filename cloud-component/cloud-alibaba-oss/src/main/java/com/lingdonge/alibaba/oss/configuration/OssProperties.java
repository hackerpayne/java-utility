package com.lingdonge.alibaba.oss.configuration;

import com.lingdonge.core.bean.base.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "alibaba.oss")
@Getter
@Setter
public class OssProperties extends BaseEntity {
    private String accessKeyId;

    private String accessKeySecret;

    private String uploadEnpoint;

    private String downloadEnpoint;

    private String bucketName;

    private String baseDir;

    private String tmpDir;

}
