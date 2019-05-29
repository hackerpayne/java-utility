package com.lingdonge.rabbit.annotation;


import com.lingdonge.rabbit.configuration.RabbitMQAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 RabbitMQ的配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RabbitMQAutoConfiguration.class})
@Documented
public @interface EnableKyleRabbitMQ {
}