package com.lingdonge.redis.service;

import com.lindonge.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/**
 * Redis实现的队列，
 * 需要包括：添加到队列、判断是否存在、清空队列
 * Created by kyle on 2017/7/7.
 */
@Slf4j
public class RedisQueueUtil {

    private String redisKey;

    private RedisPoolUtil redis;

    public RedisQueueUtil(RedisProperties redisProperties, String key) {
        this.redis = new RedisPoolUtil(redisProperties);
        this.redisKey = key;
    }

    /**
     * 判断元素是否在列表内
     *
     * @param value
     * @return
     */
    public boolean exists(String value) {
        return redis.sismember(redisKey, value);
    }

    /**
     * 随机取出一个值，并删除里面的数据
     *
     * @return
     */
    public String pop() {
        return redis.spop(redisKey);
    }

    /**
     * 随机取出一个值，但是不会删除
     *
     * @return
     */
    public String get() {
        return redis.srandmember(redisKey);
    }

    /**
     * 删除一个指定的值
     *
     * @param value
     * @return
     */
    public Long delete(String... value) {
        return redis.srem(redisKey, value);
    }

    /**
     * 清空队列里面的数据
     *
     * @return
     */
    public boolean clear() {
        return redis.delete(redisKey);
    }

    /**
     * 往队列里面添加一条数据
     *
     * @param value
     * @return
     */
    public long add(String value) {
        return redis.sadd(redisKey, value);
    }

    /**
     * 当前队列的长度
     *
     * @return
     */
    public Long count() {
        return redis.scard(redisKey);
    }

    public static void main(String[] args) throws Exception {

        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setHost("localhost");
        redisProperties.setPassword("");
        RedisQueueUtil queue = new RedisQueueUtil(redisProperties, "test_queue");

        for (int i = 0; i < 100; i++) {
            queue.add("Test:" + String.valueOf(i));
        }
        System.out.println(StringUtils.format("当前队列数量：{}", queue.count()));
        System.out.println(StringUtils.format("当前队列是否存在Test:1：{}", queue.exists("Test:1") ? "yes" : "no"));
        System.out.println(StringUtils.format("当前队列是否存在Test:101：{}", queue.exists("Test:101") ? "yes" : "no"));

        queue.delete("Test:1");
        queue.delete("Test:2");
        queue.delete("Test:3");

        System.out.println(StringUtils.format("删除3条后，当前队列数量：{}", queue.count()));

        System.out.println(StringUtils.format("检查Test:1是否存在，结果为：{}", queue.exists("Test:1")));

    }
}
