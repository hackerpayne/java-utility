package com.lingdonge.redis;

import com.lingdonge.redis.service.RedisPoolUtil;

/**
 * Created by kyle on 2017/6/30.
 */
public class RedisManagerThreadTest implements Runnable {

    private Integer threadId;

    public RedisManagerThreadTest(Integer i) {
        threadId = i;
    }

    @Override
    public void run() {
        RedisPoolUtil redis = null;
        try {
            redis = RedisPoolUtil.getInstance();
            redis.set("key:" + threadId, "haha====" + threadId);
            String data = redis.get("key:" + threadId);
            System.out.println("Data is :" + data);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}