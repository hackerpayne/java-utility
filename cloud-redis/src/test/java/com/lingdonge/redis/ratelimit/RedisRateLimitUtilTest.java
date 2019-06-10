package com.lingdonge.redis.ratelimit;

import com.lingdonge.redis.RedisConfigUtil;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisRateLimitUtilTest {

    private RedisRateLimitUtil rateLimitUtil;

    public void init() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("localhost");
        redisProperties.setPassword("123456");
        RedisTemplate redisTemplate = RedisConfigUtil.getRedisTemplateFromJedis(redisProperties);
        rateLimitUtil = new RedisRateLimitUtil(redisTemplate);
    }

    @Test
    public void testRateLimit() throws InterruptedException {

        init();

        // 频率限制
        if (!rateLimitUtil.acquireByRate("18515490061", 60L)) {
            System.out.println("60秒以内不能重复发送！");
        }
        if (!rateLimitUtil.acquireByDuration("18515490061", 300L, 3L)) {
            System.out.println("5分钟以内不能超过3条！");
        }
        if (!rateLimitUtil.acquireByDuration("18515490061", 24 * 60 * 60L, 5L)) {
            System.out.println("1天以内不能超过5条！");
        }

        Boolean canDoJob = Boolean.FALSE;
        for (Integer i = 0; i < 100; i++) {
            canDoJob = rateLimitUtil.acquireByDuration("18515490065", 300L, 3L); // 50秒以内，只能请求3次
            System.out.print("第" + i + "次");
            System.out.println(canDoJob ? "可以使用-OK" : "不可使用！！！");
            Thread.sleep(1 * 1000);
        }
    }
}