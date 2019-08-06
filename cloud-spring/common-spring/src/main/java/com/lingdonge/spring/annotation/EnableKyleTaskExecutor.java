package com.lingdonge.spring.annotation;

import com.lingdonge.spring.configuration.TaskExecutorAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 线程池管理类
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(TaskExecutorAutoConfiguration.class)
@Documented
public @interface EnableKyleTaskExecutor {
}