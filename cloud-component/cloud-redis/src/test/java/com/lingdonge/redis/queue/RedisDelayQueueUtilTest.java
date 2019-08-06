package com.lingdonge.redis.queue;

import cn.hutool.core.thread.ThreadUtil;
import com.lingdonge.core.dates.LocalDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.time.Duration;
import java.util.Set;

@Slf4j
public class RedisDelayQueueUtilTest {

    RedisDelayQueueUtil redisDelayQueueUtil;

    private String redisKey = "redis:delay:queue:test";

    public void init() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("127.0.0.1");
        redisProperties.setPassword("123456");
        redisProperties.setPort(6379);
        redisProperties.setTimeout(Duration.ofSeconds(10));
        redisDelayQueueUtil = new RedisDelayQueueUtil(redisProperties);
    }

    @Test
    public void add() {
        init();

        for (int i = 0; i < 100; i++) {
            redisDelayQueueUtil.add(redisKey, "test" + i);
            log.info("时间【{}】添加数据：【{}】", LocalDateUtil.getNowTime(), i);
            ThreadUtil.sleep(1000);
        }

    }

    @Test
    public void get() {
    }

    @Test
    public void count() {
    }

    @Test
    public void delete() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void pop() {

        init();

        while (true) {
            Set<String> listDatas = redisDelayQueueUtil.pop(redisKey, 0);
            log.info("时间【{}】扫出数据：", LocalDateUtil.getNowTime());
            listDatas.forEach(item -> System.out.println(item));

            ThreadUtil.sleep(1000);

        }

    }
}