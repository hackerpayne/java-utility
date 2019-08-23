package com.lingdonge.redis.queue;

import com.lingdonge.redis.RedisTestUtil;
import com.lingdonge.redis.util.RedissonUtil;
import org.junit.Before;
import org.junit.Test;
import org.redisson.api.RDeque;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;

public class RedissonDelayQueueUtilTest {

    RedissonClient redissonClient;

    @Before
    public void init() {
        redissonClient = RedissonUtil.getRedissonClient(RedisTestUtil.buildRedisProperties());
    }

    @Test
    public void testDelayQueue() {
        RQueue<String> blockingQueue = redissonClient.getBlockingQueue("dest_queue1");
        RDeque<String> delayedQueue = redissonClient.getDeque("test");

        // move object to blockingQueue in 10 seconds
//        delayedQueue.offer("msg1", 10, TimeUnit.SECONDS);

        // move object to blockingQueue in 1 minutes
//        delayedQueue.offer("msg2", 1, TimeUnit.MINUTES);
    }
}