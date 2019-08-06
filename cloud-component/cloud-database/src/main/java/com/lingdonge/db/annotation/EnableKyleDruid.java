package com.lingdonge.db.annotation;

import com.lingdonge.db.configuration.DruidMonitorAutoConfiguration;
import com.lingdonge.db.configuration.DruidSimpleAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 DruidConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({DruidSimpleAutoConfiguration.class, DruidMonitorAutoConfiguration.class})
@Documented
public @interface EnableKyleDruid {
}