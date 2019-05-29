package com.lingdonge.redis.configuration.custom;

import com.lingdonge.redis.RedisConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 集群Sentinel环境配置
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@Slf4j
public class RedisSentinelAutoConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * 配置redis的哨兵
     *
     * @return RedisSentinelConfiguration
     */
    @Bean
    public RedisSentinelConfiguration sentinelConfiguration() {
        return RedisConfigUtil.buildRedisSentinelConfiguration(redisProperties);
    }

    /**
     * 配置工厂
     *
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(sentinelConfiguration(), RedisConfigUtil.getJedisPoolConfig(redisProperties));
    }

    /**
     * 实例化 RedisTemplate 对象
     *
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("<<<<<<<<<<<<<<< 加载 RedisTemplate 服务 >>>>>>>>>>>>>>>>>>");
        return RedisConfigUtil.getRedisTemplate(redisConnectionFactory);
    }
}
