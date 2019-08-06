package com.lingdonge.spring.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Jwt签名配置
 */
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {

    /**
     * 发行者是程序名称，所以需要设置一个
     */
    private String appName = "";

    /**
     * Jwt加密密钥
     */
    private String secret = "kylexyz";

    /**
     * 全局过期时间，如果没有设置的时候使用这个过期时间，单位是秒
     */
    private Long expires;
}
