package com.lingdonge.spring.annotation;

import com.lingdonge.spring.configuration.FastJsonAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 FastJsonConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FastJsonAutoConfiguration.class)
@Documented
public @interface EnableKyleFastJson {
}