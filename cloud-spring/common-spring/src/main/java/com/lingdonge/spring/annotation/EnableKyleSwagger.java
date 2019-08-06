package com.lingdonge.spring.annotation;

import com.lingdonge.spring.configuration.SwaggerAPIAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启动Swagger API文档
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SwaggerAPIAutoConfiguration.class)
@Documented
public @interface EnableKyleSwagger {
}