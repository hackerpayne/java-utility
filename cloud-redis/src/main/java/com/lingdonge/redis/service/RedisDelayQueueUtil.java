package com.lingdonge.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * 聚合器
 */
public class RedisDelayQueueUtil {

    private RedisTemplate<String, String> redisTemplate;

    private ZSetOperations<String, String> zSetOperations;

    /**
     * constructor
     *
     * @param redisTemplate
     */
    public RedisDelayQueueUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    /**
     * 存入一条数据到sorted set
     *
     * @param key
     * @param event
     */
    public Boolean zset(String key, String event) {
        long now = System.currentTimeMillis();
        return zSetOperations.add(key, event, now);
    }

    /**
     * 取出整个set的所有记录
     * 筛选出过期时间范围内的所有记录
     *
     * @param key       Redis的Key值
     * @param expireSec 过期时间，单位秒
     * @return
     */
    public Set<String> zgetAll(String key, long expireSec) {
        long now = System.currentTimeMillis();
        long tts = now - expireSec * 1000;

        //下标用-1才能表示最大值  score和count要用-inf和+inf
        //return zSetOperations.rangeByScore(key, tts+1, -1);

        return zSetOperations.rangeByScore(key, tts + 1, Long.MAX_VALUE);
    }

    /**
     * 查看匹配数目
     *
     * @param key       Redis的Key值
     * @param expireSec 过期时间 单位是秒
     * @return
     */
    public Long zCount(String key, long expireSec) {
        long now = System.currentTimeMillis();
        long tts = now - expireSec * 1000;

        //下标用-1才能表示最大值  score和count要用-inf和+inf
        //return zSetOperations.count(key, tts+1, -1);

        return zSetOperations.count(key, tts + 1, Long.MAX_VALUE);
    }

    /**
     * 删除一整个policyCache
     *
     * @param zsetKey
     */
    public void removeCache(String zsetKey) {
        redisTemplate.delete(zsetKey);
    }

    /**
     * 移出过期的数据
     *
     * @param redisKey
     * @param expireTime
     */
    public void removeByRange(String redisKey, Long expireTime) {
        //移除过期数据
        long now = System.currentTimeMillis();
        long tts = now - expireTime * 1000;
//        zSetOperations.removeRangeByScore(redisKey, 0, tts);
        zSetOperations.count(redisKey, tts + 1, Long.MAX_VALUE);
    }

}