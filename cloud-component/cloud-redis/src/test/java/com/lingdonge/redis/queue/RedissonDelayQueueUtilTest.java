package com.lingdonge.redis.queue;

import com.lingdonge.redis.util.RedissonUtil;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class RedissonDelayQueueUtilTest {

    RedissonClient redissonClient;

    public void init() {
        RedisProperties redissonProperties = new RedisProperties();
        redissonProperties.setHost("127.0.0.1");
        redissonProperties.setPassword("hackerpayne:hackerpayne");
        redissonProperties.setTimeout(Duration.ofSeconds(1000));
        redissonProperties.setDatabase(3);
        redissonClient = RedissonUtil.getRedissonClient(redissonProperties);
    }


    public void testDelayQueue() {
        RQueue<String> blockingQueue = redissonClient.getBlockingQueue("dest_queue1");
        RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);

        // move object to blockingQueue in 10 seconds
        delayedQueue.offer("msg1", 10, TimeUnit.SECONDS);

        // move object to blockingQueue in 1 minutes
        delayedQueue.offer("msg2", 1, TimeUnit.MINUTES);
    }
}