package com.lingdonge.redis.configuration;

import com.lingdonge.redis.distributelock.RedissonDistributeLock;
import com.lingdonge.redis.util.RedissonUtil;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Redisson自动装配
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonAutoConfiguration {

    @Resource
    private RedisProperties redisProperties;

    /**
     * 哨兵模式自动装配
     *
     * @return
     */
    @Bean
    public RedissonClient redissonSentinel() {
        return RedissonUtil.getRedissonClient(redisProperties);
    }

    /**
     * 装配locker类，并将实例注入到RedissLockUtil中
     *
     * @return
     */
    @Bean
    public RedissonDistributeLock distributedLocker() {
        return new RedissonDistributeLock(redisProperties);
    }

}
