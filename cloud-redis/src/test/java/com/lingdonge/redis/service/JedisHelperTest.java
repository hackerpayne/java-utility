package com.lingdonge.redis.service;

import com.lingdonge.redis.ClientThread;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


@Slf4j
public class JedisHelperTest {

    @Test
    public void testJedis() {

//        logger.info("开始进行测试");

//        JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
//
//        for (int i = 0; i < 10; i++) {
//            Jedis jedis = pool.getResource();
//
//            System.out.println(jedis);
//
//            if (jedis != null) jedis.close();
//        }


        for (int i = 0; i < 100; i++) {
            ClientThread t = new ClientThread(i);
            t.start();
        }
    }


}