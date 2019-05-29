package com.lingdonge.redis.configuration.custom;

import com.lingdonge.redis.RedisConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import javax.annotation.Resource;

/**
 * 基础的Redis配置类
 */
@Configuration
@Slf4j
public class JedisBasicAutoConfiguration {

    /**
     * 配置文件内容明细
     */
    @Resource
    private RedisProperties redisProperties;

    /**
     * 手动构造连接池，用于设置一些自定义参数的时候使用
     *
     * @return
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return RedisConfigUtil.getJedisConnectionFactory(redisProperties);
    }


}
