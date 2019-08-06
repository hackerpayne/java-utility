package com.lingdonge.redis.annotation;

import com.lingdonge.redis.configuration.RedissonAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 RedissonAutoConfiguration
 * 启用Redisson分布式锁
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RedissonAutoConfiguration.class})
@Documented
public @interface EnableKyleRedisson {
}