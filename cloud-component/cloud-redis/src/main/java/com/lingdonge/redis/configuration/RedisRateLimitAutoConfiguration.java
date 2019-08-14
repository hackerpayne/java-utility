package com.lingdonge.redis.configuration;

import com.lingdonge.redis.ratelimit.RedisRateLimitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 初始化Redis限流器
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
public class RedisRateLimitAutoConfiguration {

    @Resource
    private RedisProperties redisProperties;

    @Bean
    public RedisRateLimitUtil redisRateLimitUtil() {
        log.info("<<<<<<<<<<<<<<< 加载 RedisRateLimitUtil限流器 服务 >>>>>>>>>>>>>>>>>>");
        return new RedisRateLimitUtil(redisProperties);
    }

}
