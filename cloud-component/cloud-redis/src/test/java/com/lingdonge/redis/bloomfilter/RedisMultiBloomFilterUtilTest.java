package com.lingdonge.redis.bloomfilter;

import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

public class RedisMultiBloomFilterUtilTest {

    private RedisMultiBloomFilterUtil redisMultiBloomFilterUtil;

    public void init() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("127.0.0.1");
        redisProperties.setPassword("123456");
        redisProperties.setDatabase(1);

        redisMultiBloomFilterUtil = new RedisMultiBloomFilterUtil(redisProperties, 3);
    }

    @Test
    public void contains() {

        init();

//        System.out.println("add添加结果：");
//        System.out.println(redisMultiBloomFilterUtil.add("18515490065"));

        if (redisMultiBloomFilterUtil.contains("18515490065")) {
            System.out.println("contains");
        } else {
            System.out.println("no contains");
        }
    }

    @Test
    public void add() {

        init();

        redisMultiBloomFilterUtil.add("18515490065");
    }
}