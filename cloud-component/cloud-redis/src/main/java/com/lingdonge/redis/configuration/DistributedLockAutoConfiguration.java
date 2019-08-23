package com.lingdonge.redis.configuration;

import com.lingdonge.redis.distributelock.DistributeLock;
import com.lingdonge.redis.distributelock.RedisTemplateLock;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 分布式锁自动配置
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class DistributedLockAutoConfiguration {

    /**
     * 创建Bean
     *
     * @param redisTemplate
     * @return
     */
    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public DistributeLock redisDistributedLock(RedisTemplate<Object, Object> redisTemplate) {
        return new RedisTemplateLock(redisTemplate);
    }

}
