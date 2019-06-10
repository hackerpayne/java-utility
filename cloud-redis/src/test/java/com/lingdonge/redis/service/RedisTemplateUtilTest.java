package com.lingdonge.redis.service;

import com.lingdonge.redis.SpringBaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.List;

@EnableAutoConfiguration
@Slf4j
public class RedisTemplateUtilTest extends SpringBaseTest {

    private RedisTemplateUtil redisTemplateUtil;

    public void init() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("127.0.0.1");
        redisProperties.setPassword("123456");
        redisProperties.setPort(6379);
        redisTemplateUtil = new RedisTemplateUtil(redisProperties);
    }

    @Test
    public void testSet() {
        init();

        boolean result = redisTemplateUtil.set("testKey", "TestValue");
        System.out.println(result);
        String value = redisTemplateUtil.get("testKey").toString();
        System.out.println("结果为：" + value);
    }

    @Test
    public void testScan() {

        init();

        List<String> listKeys = redisTemplateUtil.keys("f*");
        for (String cursor : listKeys) {
            System.out.println(cursor);
        }

    }

}
