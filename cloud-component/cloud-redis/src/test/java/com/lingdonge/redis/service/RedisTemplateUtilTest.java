package com.lingdonge.redis.service;

import com.lingdonge.redis.RedisTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

//@EnableAutoConfiguration
@Slf4j
public class RedisTemplateUtilTest {

    private RedisTemplateUtil redisTemplateUtil;

    @Before
    public void init() {
        redisTemplateUtil = new RedisTemplateUtil(RedisTestUtil.buildRedisProperties());
    }

    @Test
    public void testSet() {
        boolean result = redisTemplateUtil.set("testKey", "TestValue");
        System.out.println(result);
        String value = redisTemplateUtil.get("testKey").toString();
        System.out.println("结果为：" + value);
    }

    @Test
    public void testScan() {

        List<String> listKeys = redisTemplateUtil.keys("f*");
        for (String cursor : listKeys) {
            System.out.println(cursor);
        }

    }

    @Test
    public void testInteger() {
//        Long incre = redisTemplateUtil.incr("test:test2", 1);
//        log.info("添加结果：{}", incre);

        redisTemplateUtil.set("test:test2", 12343);

//        Long getIncre = (Long) redisTemplateUtil.get("test:test2");
//        log.info("获取Incre之后的结果1：{}", getIncre);

        Long getIncre2 = redisTemplateUtil.getNumber("test:test2").longValue();
        log.info("获取Incre之后的结果2：{}", getIncre2);

        Integer getIncre3 = redisTemplateUtil.getNumber("test:test2").intValue();
        log.info("获取Incre之后的结果3：{}", getIncre3);
    }

}
