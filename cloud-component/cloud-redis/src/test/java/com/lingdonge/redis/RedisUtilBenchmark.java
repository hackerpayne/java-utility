package com.lingdonge.redis;

import com.lingdonge.redis.service.RedisPoolUtil;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RedisUtilBenchmark {
    private static final int TOTAL_OPERATIONS = 100000;

    public static void main(String[] args) throws Exception {
        long t = System.currentTimeMillis();
        // dotest();
        lpush();
        long elapsed = System.currentTimeMillis() - t;
        System.out.println(((1000 * TOTAL_OPERATIONS) / elapsed) + " ops");
    }

    private static void dotest() throws Exception {

        final RedisPoolUtil redisPoolUtil = new RedisPoolUtil(RedisTestUtil.buildRedisProperties());

        List<Thread> tds = new ArrayList<Thread>();

        final Integer temp = 0;
        final AtomicInteger ind = new AtomicInteger();
        for (int i = 0; i < 50; i++) {
            Thread hj = new Thread(new Runnable() {
                public void run() {
                    for (int i = 0; (i = ind.getAndIncrement()) < TOTAL_OPERATIONS; ) {

                        // Jedis j = pool.getResource();
                        final String key = "foo" + i;

                        // redisUtil.set(key, key);
                        redisPoolUtil.set(key, temp);
                    }
                }
            });
            tds.add(hj);
            hj.start();
        }

        for (Thread t : tds)
            t.join();

    }

    private static void lpush() throws Exception {
        final RedisPoolUtil redisPoolUtil = new RedisPoolUtil();

        UserRequestRecord record = new UserRequestRecord();
        record.setMobile("1301387677");
        record.setTimestamp(System.currentTimeMillis());
        redisPoolUtil.lpush("test1", record);
    }

    /**
     * 用户访问记录
     *
     * @category @author xiangyong.ding@weimob.com
     * @since 2017年3月30日 下午4:20:47
     */
    public static class UserRequestRecord {
        /**
         * 手机号，唯一标志用户身份
         */
        private String mobile;

        /**
         * 时间戳
         */
        private long timestamp;

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("UserRequestRecord [mobile=");
            builder.append(mobile);
            builder.append(", timestamp=");
            builder.append(timestamp);
            builder.append("]");
            return builder.toString();
        }

    }
}