package com.lingdonge.redis.ratelimit;

import com.lingdonge.core.threads.ThreadUtil;
import com.lingdonge.redis.util.RedisConnUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class RedisRateLimitUtilTest {

    private RedisRateLimitUtil rateLimitUtil;

    public void init() {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("localhost");
        redisProperties.setPassword("123456");
        RedisTemplate redisTemplate = RedisConnUtil.getRedisTemplateFromLettuce(redisProperties);
        rateLimitUtil = new RedisRateLimitUtil(redisTemplate);
    }

    @Test
    public void testRateLimit() throws InterruptedException {

        init();

        for (Integer i = 0; i < 100; i++) {

            // 频率限制
            if (!rateLimitUtil.acquireByRate("18515490061", 60L)) {
                log.info("5秒以内不能重复发送！[{}]", i);
                ThreadUtil.sleep(1000);
                continue;
            } else {
                log.info("当前状态可用：{}", i);
            }
            if (!rateLimitUtil.acquireByDuration("18515490061", 30L, 3L)) {
                log.info("30秒以内不能超过3条！[{}]", i);
                ThreadUtil.sleep(1000);
                continue;
            } else {
                log.info("当前状态可用：{}", i);
            }
            if (!rateLimitUtil.acquireByDuration("18515490061", 24 * 60 * 60L, 5L)) {
                log.info("1天以内不能超过5条！[{}]", i);
                ThreadUtil.sleep(1000);
                continue;
            } else {
                log.info("当前状态可用：{}", i);
            }

            ThreadUtil.sleep(1000);
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