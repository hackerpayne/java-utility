package com.lingdonge.redis.annotation;

import com.lingdonge.redis.configuration.RedisBloomFilterAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动启用 RedisBloomFilterAutoConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RedisBloomFilterAutoConfiguration.class})
@Documented
public @interface EnableRedisBloomFilter {
}