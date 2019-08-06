package com.lingdonge.spring.annotation;

import com.lingdonge.spring.web.listener.SpringServerConfigUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 SpringServerConfigUtil 配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SpringServerConfigUtil.class)
@Documented
public @interface EnableKyleSpringServerConfig {
}