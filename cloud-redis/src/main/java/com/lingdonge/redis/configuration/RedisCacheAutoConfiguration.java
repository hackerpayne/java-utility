package com.lingdonge.redis.configuration;

import com.lingdonge.redis.RedisConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 构造Redis缓存架构
 */
//@Configuration
@AutoConfigureAfter(RedisBasicAutoConfiguration.class) //在这个配置之后进行加载
@EnableCaching//启用缓存，这个注解很重要；
@Slf4j
public class RedisCacheAutoConfiguration extends CachingConfigurerSupport {

    /**
     * 自定义key.使用默认的容易出现乱码
     * 此方法将会根据类名+方法名+所有参数的值生成唯一的一个key,即使@Cacheable中的value属性一样，key也会不一样。
     * 普通使用普通类的方式的话，那么在使用@Cacheable的时候还需要指定KeyGenerator的名称;这样编码的时候比较麻烦。
     */
    @Override
    public KeyGenerator keyGenerator() {
        log.info("<<<<<<<<<<<<<<< 加载 RedisCacheConfig.keyGenerator自定义生成Key  >>>>>>>>>>>>>>>>>>");
        return RedisConfigUtil.getCacheKeyGenerater();
    }

    /**
     * 使用cache注解管理redis缓存
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisConfigUtil.getCacheManager(redisConnectionFactory);
    }


}
