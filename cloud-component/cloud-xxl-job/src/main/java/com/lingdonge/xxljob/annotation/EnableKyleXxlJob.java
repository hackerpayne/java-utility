package com.lingdonge.xxljob.annotation;

import com.lingdonge.xxljob.configuration.XxlJobConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启动 Xxl-Job
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(XxlJobConfiguration.class)
@Documented
public @interface EnableKyleXxlJob {
}