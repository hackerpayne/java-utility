package com.lingdonge.redis.bloomfilter;

import com.lingdonge.redis.RedisTestUtil;
import org.junit.Before;
import org.junit.Test;

public class RedisMultiBloomFilterUtilTest {

    private RedisMultiBloomFilterUtil redisMultiBloomFilterUtil;

    @Before
    public void init() {
        redisMultiBloomFilterUtil = new RedisMultiBloomFilterUtil(RedisTestUtil.buildRedisProperties(), 3);
    }

    @Test
    public void contains() {

        init();

//        System.out.println("add添加结果：");
//        System.out.println(redisMultiBloomFilterUtil.add("18515490065"));

        if (redisMultiBloomFilterUtil.contains("18515490065")) {
            System.out.println("contains");
        } else {
            System.out.println("no contains");
        }
    }

    @Test
    public void add() {

        init();

        redisMultiBloomFilterUtil.add("18515490065");
    }
}