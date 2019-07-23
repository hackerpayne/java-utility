package com.lingdonge.springcloud.annotation;

import com.lingdonge.springcloud.configuration.SwaggerZuulAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启动Swagger API文档
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SwaggerZuulAutoConfiguration.class)
@Documented
public @interface EnableKyleSwaggerZuul {
}