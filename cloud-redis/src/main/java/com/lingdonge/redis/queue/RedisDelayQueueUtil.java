package com.lingdonge.redis.queue;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.SystemClock;
import com.lingdonge.redis.util.RedisConnUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Set;

/**
 * Redis延时队列：利用SortedSet的特性：使用redis的zset有序性，轮询zset中的每个元素，到点后将内容迁移至待消费的队列
 * <p>
 * 入队时：
 * key: id Value是想执行的时间戳：比如
 * <p>
 * 扫描时：扫描score 小于当前时间的数据，即为结果
 */
@Slf4j
public class RedisDelayQueueUtil {

    private RedisTemplate<String, String> redisTemplate;

    private ZSetOperations<String, String> zSetOperations;

    /**
     * 直接从Redis配置中创建连接
     *
     * @param redisProperties
     */
    public RedisDelayQueueUtil(RedisProperties redisProperties) {

        RedisAutoConfiguration redisAutoConfiguration;
        this.redisTemplate = RedisConnUtil.getRedisTemplate(redisProperties);
        log.info("Redis:{}",redisTemplate.hasKey("tests1"));
        this.zSetOperations = redisTemplate.opsForZSet();
    }

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
     * 存入一条数据到sorted set，其中score是当前时间戳，延时多长时间是后续逻辑来判断，此处只负责添加数据即可。
     *
     * @param key   RedisKey
     * @param event 事件数据
     */
    public Boolean add(String key, String event) {
        long now = SystemClock.now();
        return zSetOperations.add(key, event, now);
    }

    /**
     * 取出整个set的所有记录，筛选出过期时间范围内的所有记录：
     * 比如录的时间是：2019-06-10 00：00：00 ， 现在是时间：00：01：00，要获取1小时前添加的所有数据，这里的过期时间即为60*60，单位是秒。实现延时队列获取信息
     *
     * @param key       Redis的Key值
     * @param expireSec 过期时间，单位秒
     * @return
     */
    public Set<String> get(String key, long expireSec) {
        long now = SystemClock.now();
        long tts = now - expireSec * 1000;

        //下标用-1才能表示最大值  score和count要用-inf和+inf
        //return zSetOperations.rangeByScore(key, tts+1, -1);
        return zSetOperations.rangeByScore(key, tts + 1, Long.MAX_VALUE);
    }

    /**
     * 取出数据并弹出结果
     * @param key
     * @param expireSec
     * @return
     */
    public Set<String> pop(String key, long expireSec) {
        long now = SystemClock.now();
        long tts = now - expireSec * 1000;

        //下标用-1才能表示最大值  score和count要用-inf和+inf
        //return zSetOperations.rangeByScore(key, tts+1, -1);
        Set<String> datas = zSetOperations.rangeByScore(key, tts + 1, Long.MAX_VALUE);

        if (CollUtil.isEmpty(datas)) {
            return CollUtil.newHashSet();
        }
        zSetOperations.remove(key, datas.toArray()); // 删除这些数据
        return datas;
    }

    /**
     * 查看匹配数目
     *
     * @param key       Redis的Key值
     * @param expireSec 过期时间 单位是秒
     * @return
     */
    public Long count(String key, long expireSec) {
        long now = SystemClock.now();
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
    public void delete(String zsetKey) {
        redisTemplate.delete(zsetKey);
    }

    /**
     * 移出过期的数据
     *
     * @param redisKey
     * @param expireTime
     */
    public void remove(String redisKey, Long expireTime) {
        //移除过期数据
        long now = SystemClock.now();
        long tts = now - expireTime * 1000;
        zSetOperations.removeRangeByScore(redisKey, 0, tts);
//        zSetOperations.count(redisKey, tts + 1, Long.MAX_VALUE);
    }

}