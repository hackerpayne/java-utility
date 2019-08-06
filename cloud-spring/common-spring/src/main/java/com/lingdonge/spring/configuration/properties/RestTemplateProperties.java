package com.lingdonge.spring.configuration.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "http")
@Data
public class RestTemplateProperties {

    /**
     * 超时时间，单位是秒
     */
    private Integer timeOutSeconds = 30;

    /**
     * 每个路由网址的连接数大小，默认50
     */
    private Integer maxPerRoute = 50;

    /**
     * 总链接大小，默认200
     */
    private Integer maxTotal = 200;
}
