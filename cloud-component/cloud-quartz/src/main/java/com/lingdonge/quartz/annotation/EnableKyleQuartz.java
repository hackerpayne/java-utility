package com.lingdonge.quartz.annotation;

import com.lingdonge.quartz.configuration.QuartzConfiguration;
import com.lingdonge.quartz.configuration.QuartzJobFactory;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 QuartzConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({QuartzJobFactory.class, QuartzConfiguration.class})
@Documented
public @interface EnableKyleQuartz {
}