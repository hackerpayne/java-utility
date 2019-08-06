package com.lingdonge.spring.annotation;

import com.lingdonge.spring.SpringContextUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 SpringContextUtils
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({SpringContextUtil.class})
@Documented
public @interface EnableKyleSpring {
}