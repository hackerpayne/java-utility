package com.lingdonge.redis;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class JedisHelperTest {

    public static void main(String[] args) {

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