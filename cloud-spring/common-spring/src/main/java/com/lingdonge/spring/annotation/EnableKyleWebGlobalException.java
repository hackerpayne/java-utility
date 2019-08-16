package com.lingdonge.spring.annotation;

import com.lingdonge.spring.exception.handler.GlobalHandlerExceptionResolver;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 全局异常处理 功能
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(GlobalHandlerExceptionResolver.class)
@Documented
public @interface EnableKyleWebGlobalException {
}