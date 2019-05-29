package com.lingdonge.spring.annotation;

import com.lingdonge.spring.configuration.EventBusConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 EventBus事件总线，使用Google Guava实线
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Import({EventBusConfiguration.class, EventSubscribeBeanPostProcessor.class})
@Import({EventBusConfiguration.class})
@Documented
public @interface EnableKyleEventBus {
}