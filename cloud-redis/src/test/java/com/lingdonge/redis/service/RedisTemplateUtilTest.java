package com.lingdonge.redis.service;

import com.lingdonge.redis.service.RedisTemplateUtil;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.List;

//@RunWith(SpringJunit4ClassRunner.class)
//@SpringBootTest
public class RedisTemplateUtilTest {

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
