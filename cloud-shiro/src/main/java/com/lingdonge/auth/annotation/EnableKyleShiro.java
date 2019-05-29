package com.lingdonge.auth.annotation;

import com.lingdonge.auth.configuration.ShiroAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 使用普通的Shiro管理
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ShiroAutoConfiguration.class)
@Documented
public @interface EnableKyleShiro {
}