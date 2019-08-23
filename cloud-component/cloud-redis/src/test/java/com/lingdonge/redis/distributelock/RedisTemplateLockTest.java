package com.lingdonge.redis.distributelock;

import com.lingdonge.redis.RedisTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisTemplateLockTest {

    private RedisTemplateLock redisTemplateLock;

    @Before
    public void init() {
        redisTemplateLock = new RedisTemplateLock(RedisTestUtil.buildRedisProperties());
    }

    /**
     * 模拟20个线程下单
     */
    @Test
    public void testWithThreads() {
        for (int i = 0; i < 20; i++) {
            CompletableFuture.runAsync(() -> {
                handleOder();
            });
        }
    }

    private final int MAX = 10;
    int n = 10;

    @Test
    public void handleOder() {

        String userName = UUID.randomUUID().toString().substring(0, 8) + Thread.currentThread().getName();

        redisTemplateLock.lock("Huawei Mate 10：" + userName, 10000L);
        System.out.println("正在为用户：" + userName + " 处理订单");

        if (n > 0) {
            int num = MAX - n + 1;
            System.out.println("用户：" + userName + "购买第" + num + "台，剩余" + (--n) + "台");
        } else {
            System.out.println("用户：" + userName + "无法购买，已售罄！");
        }

        redisTemplateLock.releaseLock("Huawei Mate 10：" + userName);
    }

    @Test
    public void test() throws InterruptedException {

        String key1 = "hahahahah";
        boolean b1 = redisTemplateLock.lock(key1, 10000L);
        log.info("b1 is : {}", b1);

        TimeUnit.SECONDS.sleep(5);

//        String key2 = "testestes";
        boolean b2 = redisTemplateLock.lock(key1, 5000L);
        log.info("b2 is : {}", b2);

        redisTemplateLock.releaseLock(key1);
//        redisTemplateLock.releaseLock(key2);
    }

    @Test
    public void lock() {
    }

    @Test
    public void releaseLock() {
    }
}