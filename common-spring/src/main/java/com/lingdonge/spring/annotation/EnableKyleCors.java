package com.lingdonge.spring.annotation;

import com.lingdonge.spring.configuration.CorsAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 CORSConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CorsAutoConfiguration.class)
@Documented
public @interface EnableKyleCors {
}