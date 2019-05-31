package com.lingdonge.redis;

import com.lingdonge.redis.service.RedisPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@EnableAutoConfiguration
@Slf4j
public class RedisPoolUtilTest extends SpringBaseTest {

    @Test
    public void testMultiThread() {

        // 10线程测试Redis连接情况
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);

        for (int i = 1; i <= 100; i++) {
            final int index = i;
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        String threadName = Thread.currentThread().getName();
                        log.info("执行：" + index + "，线程名称：" + threadName);

                        RedisPoolUtil redisPoolUtil = RedisPoolUtil.getInstance();
                        for (int p = 0; p < 100; p++) {

                            log.info("执行赋值：" + p + "，线程名称：" + threadName);

                            Jedis jedis = redisPoolUtil.getJedis();

                            jedis.hset("multithreading:" + p, threadName + ":" + String.valueOf(p), String.valueOf(p));

                            RedisPoolUtil.close(jedis);
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            });
        }

        //关闭线程池
        fixedThreadPool.shutdown();

        try {
            boolean loop = true;
            do { // 等待所有任务完成
                loop = !fixedThreadPool.awaitTermination(2, TimeUnit.SECONDS);
            } while (loop);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
