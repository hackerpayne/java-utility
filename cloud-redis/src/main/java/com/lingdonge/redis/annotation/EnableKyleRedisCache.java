package com.lingdonge.redis.annotation;

import com.lingdonge.redis.configuration.RedisBasicAutoConfiguration;
import com.lingdonge.redis.configuration.RedisCacheAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 RedisAutoConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RedisBasicAutoConfiguration.class, RedisCacheAutoConfiguration.class})
@Documented
public @interface EnableKyleRedisCache {
}