package com.lingdonge.redis.configuration;

import com.lingdonge.redis.util.RedisConnUtil;
import com.lingdonge.redis.service.RedisTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@Configuration
@Slf4j
public class RedisBasicAutoConfiguration {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

//    @Resource
//    private RedisProperties redisProperties;

    /**
     * 实例化 RedisTemplate 对象
     *
     * @return
     */
    @Bean
    public RedisTemplate redisTemplate() {
        log.info("<<<<<<<<<<<<<<< 加载 RedisTemplate 服务 >>>>>>>>>>>>>>>>>>");
        return RedisConnUtil.getRedisTemplate(redisConnectionFactory);
    }

    /**
     * 加载 RedisService 服务,注入封装RedisTemplate
     *
     * @return
     */
    @Bean
    public RedisTemplateUtil redisTemplateUtil() {
        log.info("<<<<<<<<<<<<<<< 加载 RedisTemplateUtil 服务 >>>>>>>>>>>>>>>>>>");
        return new RedisTemplateUtil(redisTemplate());
    }
//
//    /**
//     * 构建RedisUtil的Bean实例
//     *
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public RedisPoolUtil redisPoolUtil() {
//        log.info("<<<<<<<<<<<<<<< 加载 RedisPoolUtil 服务 >>>>>>>>>>>>>>>>>>");
//        return new RedisPoolUtil(redisProperties);
//    }

}
