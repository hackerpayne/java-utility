package com.lingdonge.redis.annotation;

import com.lingdonge.redis.configuration.RedisBasicAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 RedisBasicAutoConfiguration
 * 只使用RedisTemplate和Redis相关的服务
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RedisBasicAutoConfiguration.class})
@Documented
public @interface EnableKyleRedis {
}