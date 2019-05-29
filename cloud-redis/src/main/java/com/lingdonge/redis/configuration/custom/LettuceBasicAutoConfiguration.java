package com.lingdonge.redis.configuration.custom;

import com.lingdonge.redis.RedisConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 使用Lettuce做为客户端连接
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Slf4j
public class LettuceBasicAutoConfiguration {


    @Resource
    private RedisProperties redisProperties;


    /**
     * 生成Lettuce连接池
     *
     * @return
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        return RedisConfigUtil.getLettuceConnectionFactory(redisProperties);
    }

    /**
     * Redis模板操作类
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate(LettuceConnectionFactory connectionFactory) {
        log.info("<<<<<<<<<<<<<<< 加载 RedisTemplate 服务 >>>>>>>>>>>>>>>>>>");
        return RedisConfigUtil.getRedisTemplate(connectionFactory);

    }

}
