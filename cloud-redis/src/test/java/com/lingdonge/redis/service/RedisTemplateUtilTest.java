package com.lingdonge.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.List;

//@EnableAutoConfiguration
@Slf4j
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

    @Test
    public void testInteger() {
        init();

//        Long incre = redisTemplateUtil.incr("test:test2", 1);
//        log.info("添加结果：{}", incre);

        redisTemplateUtil.set("test:test2", 10);

//        Long getIncre = (Long) redisTemplateUtil.get("test:test2");
//        log.info("获取Incre之后的结果1：{}", getIncre);

        Long getIncre2 = redisTemplateUtil.getNumber("test:test2").longValue();
        log.info("获取Incre之后的结果2：{}", getIncre2);

        Integer getIncre3 = redisTemplateUtil.getNumber("test:test2").intValue();
        log.info("获取Incre之后的结果3：{}", getIncre3);
    }

}
