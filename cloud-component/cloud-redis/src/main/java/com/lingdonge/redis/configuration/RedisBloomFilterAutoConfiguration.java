package com.lingdonge.redis.configuration;

import com.lingdonge.redis.bloomfilter.RedisBloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@Slf4j
public class RedisBloomFilterAutoConfiguration {

    @Resource
    private RedisProperties redisProperties;

    /**
     * 启用BloomFilter过滤器
     *
     * @return
     */
    @Bean
    public RedisBloomFilterService redisBloomFilterService() {
        log.info("<<<<<<<<<<<<<<< 加载 RedisBloomFilterService 服务 >>>>>>>>>>>>>>>>>>");
        return new RedisBloomFilterService(redisProperties);
    }

}
