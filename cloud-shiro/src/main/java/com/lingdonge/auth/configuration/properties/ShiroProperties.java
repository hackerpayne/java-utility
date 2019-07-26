package com.lingdonge.auth.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * shiro缓存配置注解
 */
@Component
@ConfigurationProperties(prefix = "auth")
@Data
public class ShiroProperties {

    /**
     * 缓存库
     */
    private Integer databaseShiro;

    /**
     * IP地址
     */
    private String host;

    /**
     * 密码
     */
    private String pass;

    /**
     * 端口
     */
    private Integer port;

    /**
     *
     */
    private Integer timeout;

    /**
     * 缓存过期时间
     */
    private Integer expire = 30 * 60;
    /**
     * 保存Header的时间
     */
    private String header = "Authorization";
    /**
     * 手机版过期时间
     */
    private Integer expiremobile = 30 * 60;
    /**
     * 应用程序名称，用于签发Token时使用
     */
    private String appname = "test_app";
    /**
     * 密钥
     */
    private String secret = "sdfds6234ds$#@#";

    /**
     * 未授权的用户登陆链接
     */
    private String loginUrl="/login";

    /**
     * 未授权的用户跳转链接
     */
    private String unauthorizeUrl="";
}
