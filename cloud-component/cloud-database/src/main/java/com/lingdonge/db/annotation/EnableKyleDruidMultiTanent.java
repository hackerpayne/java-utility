package com.lingdonge.db.annotation;

import com.lingdonge.db.configuration.DruidMultitanetAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 DruidMultitanetAutoConfiguration 多租户结构
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DruidMultitanetAutoConfiguration.class)
@Documented
public @interface EnableKyleDruidMultiTanent {
}