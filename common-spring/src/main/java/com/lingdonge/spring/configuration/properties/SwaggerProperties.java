package com.lingdonge.spring.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Swagger配置文件
 */
@ConfigurationProperties(prefix = "swagger")
@Data
public class SwaggerProperties {

    /**
     * 要扫描的包路径
     */
    private String packages = "*";

    /**
     * 解析结果路径
     */
    private String path = "/";

    /**
     * 标题
     */
    private String title = "标题";

    /**
     * 描述
     */
    private String description = "描述";

    /**
     * 版本号
     */
    private String version = "1.0.0";
}
