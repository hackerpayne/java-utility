package com.lingdonge.redis.ratelimit;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.TimeUnit;

public class RedisRateLimiterTest {

    private static final MetricRegistry metrics = new MetricRegistry();
    private static ConsoleReporter reporter = ConsoleReporter.forRegistry(metrics).build();
    private static final Meter requests = metrics.meter(MetricRegistry.name(RedisRateLimiterTest.class, "success"));
    private Timer timer = metrics.timer(MetricRegistry.name(RedisRateLimiterTest.class, "totalRequest"));

    @Test
    public void testRedisRateLimit() throws InterruptedException {
        reporter.start(10, TimeUnit.SECONDS);
        ApplicationContext ac = new ClassPathXmlApplicationContext("root-context.xml");
        JedisPool pool = (JedisPool) ac.getBean("jedisPool");
        RedisRateLimiter limiter = new RedisRateLimiter(pool, TimeUnit.SECONDS, 30);
        while (true) {
            boolean flag = false;
            Timer.Context context = timer.time();
            if (limiter.acquire("testMKey1")) {
                flag = true;
            }
            context.stop();
            if (flag) {
                requests.mark();
            }
            Thread.sleep(1);
        }

    }
}
