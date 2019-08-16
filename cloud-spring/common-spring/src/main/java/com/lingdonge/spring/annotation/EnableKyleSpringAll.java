package com.lingdonge.spring.annotation;

import com.lingdonge.spring.SpringContextUtil;
import com.lingdonge.spring.configuration.CorsAutoConfiguration;
import com.lingdonge.spring.configuration.HibernateValidatorAutoConfiguration;
import com.lingdonge.spring.configuration.JacksonAutoConfiguration;
import com.lingdonge.spring.exception.handler.GlobalHandlerExceptionResolver;
import com.lingdonge.spring.web.listener.SpringServerConfigUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 SpringContextUtils
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SpringContextUtil.class, SpringServerConfigUtil.class, GlobalHandlerExceptionResolver.class, CorsAutoConfiguration.class, JacksonAutoConfiguration.class, HibernateValidatorAutoConfiguration.class})
@Documented
public @interface EnableKyleSpringAll {
}