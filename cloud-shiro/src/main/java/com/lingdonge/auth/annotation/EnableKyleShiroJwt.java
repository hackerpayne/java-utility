package com.lingdonge.auth.annotation;

import com.lingdonge.auth.configuration.ShiroJwtAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;


/**
 * 使用ShiroJwt管理
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ShiroJwtAutoConfiguration.class)
@Documented
public @interface EnableKyleShiroJwt {
}