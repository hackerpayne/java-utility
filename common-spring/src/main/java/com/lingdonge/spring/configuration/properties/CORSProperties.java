package com.lingdonge.spring.configuration.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 跨域的自动配置器，前缀为cros，不填写的话，允许所有
 * cors.enabled=true
 * cors.methods=
 * cors.paths=
 * cors.domains=
 */
@ConfigurationProperties(prefix = "cors")
@Data
public class CORSProperties {

    /**
     *
     */
    private String enabled;

    /**
     *
     */
    private String methods;

    /**
     *
     */
    private String paths;

    /**
     *
     */
    private String domains;

}