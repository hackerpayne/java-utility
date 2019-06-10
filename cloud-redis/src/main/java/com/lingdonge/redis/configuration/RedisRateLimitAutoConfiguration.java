package com.lingdonge.redis.configuration;

import com.lingdonge.redis.ratelimit.RedisRateLimitUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 初始化Redis限流器
 */
@Configuration
@Slf4j
public class RedisRateLimitAutoConfiguration {

    @Resource
    private RedisTemplate redisTemplate;

    @Bean
    public RedisRateLimitUtil redisRateLimitUtil() {
        log.info("<<<<<<<<<<<<<<< 加载 RedisRateLimitUtil限流器 服务 >>>>>>>>>>>>>>>>>>");
        return new RedisRateLimitUtil(redisTemplate);
    }

}
