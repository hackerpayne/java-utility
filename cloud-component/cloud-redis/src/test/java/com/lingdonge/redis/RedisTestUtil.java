package com.lingdonge.redis;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

public class RedisTestUtil {

    /**
     * 创建本地使用的Redis环境配置
     *
     * @return
     */
    public static RedisProperties buildRedisProperties() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setDatabase(5);
        redisProperties.setHost("127.0.0.1");
        redisProperties.setPassword("123456");

        return redisProperties;
    }
}
