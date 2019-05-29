package com.lingdonge.db.annotation;

import com.lingdonge.db.configuration.FastSQLAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 FastSQL
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({FastSQLAutoConfiguration.class})
@Documented
public @interface EnableKyleFastSQL {
}