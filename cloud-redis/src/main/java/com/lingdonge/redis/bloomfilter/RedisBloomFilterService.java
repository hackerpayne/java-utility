package com.lingdonge.redis.bloomfilter;

import com.google.common.collect.Maps;
import com.lingdonge.core.cache.MemoryCounter;
import com.lingdonge.redis.service.RedisTemplateUtil;
import orestes.bloomfilter.BloomFilter;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.concurrent.ConcurrentMap;

/**
 * Redis的布隆过滤器的实现
 */
public class RedisBloomFilterService {

    /**
     * Redis的配置
     */
    private RedisProperties redisProperties;

    /**
     * Redis的服务
     */
    private RedisTemplateUtil redisService;

    /**
     * 内存计数器
     */
    private MemoryCounter memoryCounter = new MemoryCounter();

    /**
     * 保存重复数量的Redis前缀
     */
    private final String dupCountPrefix = "bloomfilter_dup_count_";

    /**
     * 保存过滤器结果的前缀
     */
    private final String dupPrefix = "bloomfilter_";

    /**
     * 布隆过滤器的内存缓存结果
     */
    private ConcurrentMap<String, BloomFilter> mapCache = Maps.newConcurrentMap();

    /**
     * Spring托管，需要默认构造函数
     */
    public RedisBloomFilterService() {
    }

    public RedisBloomFilterService(RedisProperties properties) {
        this.redisProperties = properties;
    }

    /**
     * 清空所有Redis数据
     *
     * @param redisKey
     */
    public void clear(String redisKey) {
        BloomFilter bloomFilterRedis = getBloomFilter(redisKey);
        bloomFilterRedis.clear();
    }

    /**
     * 简单判断是否包含数据
     *
     * @param redisKey
     * @param item
     * @return
     */
    public boolean contains(String redisKey, String item) {
        BloomFilter bloomFilterRedis = getBloomFilter(redisKey);
        return bloomFilterRedis.contains(item);
    }

    /**
     * 简单添加
     *
     * @param redisKey
     * @param item
     * @return
     */
    public boolean add(String redisKey, String item) {
        BloomFilter bloomFilterRedis = getBloomFilter(redisKey);
        return bloomFilterRedis.add(item);
    }

    /**
     * 如果包含，则计数器加1
     *
     * @param redisKey
     * @param item
     * @return
     */
    public boolean containsThenIncr(String redisKey, String item) {
        BloomFilter bloomFilterRedis = getBloomFilter(redisKey);
        boolean contains = bloomFilterRedis.contains(item);

        // 有记录，且需要加1，就加1，并返回有记录
        if (contains) {
            redisService.incr(dupCountPrefix + redisKey);
        }
        return contains;
    }

    /**
     * 布隆过滤器有数据的话，内存记录加1
     *
     * @param redisKey
     * @param item
     * @return
     */
    public boolean containsThenIncrInMem(String redisKey, String item) {
        BloomFilter bloomFilterRedis = getBloomFilter(redisKey);
        boolean contains = bloomFilterRedis.contains(item);

        // 有记录，且需要加1，就加1，并返回有记录
        if (contains) {
            memoryCounter.increment(dupCountPrefix + redisKey);
        }
        return contains;
    }

    /**
     * 布隆过滤器没有的话就添加进去
     *
     * @param redisKey Redis的Key
     * @param item     需要判断的数据
     * @return
     */
    public boolean notContainsThenAdd(String redisKey, String item) {
        BloomFilter bloomFilterRedis = getBloomFilter(redisKey);
        boolean contains = bloomFilterRedis.contains(item);

        // 有记录，且需要加1，就加1，并返回有记录
        if (!contains) {
            bloomFilterRedis.add(item);
        }

        return contains;
    }

    /**
     * 获取某个Key的重复数量值
     *
     * @param redisKey
     * @return
     */
    public Integer getDuplicateCount(String redisKey) {
        return NumberUtils.toInt(redisService.get(dupCountPrefix + redisKey).toString());
    }

    /**
     * 获取内存里面记录的重复数量
     *
     * @param redisKey
     * @return
     */
    public Integer getDuplicateCountInMem(String redisKey) {
        return memoryCounter.get(dupCountPrefix + redisKey);
    }

    /**
     * 根据Key值返回一个生成好的布隆过滤器
     *
     * @param redisKey
     * @return
     */
    public BloomFilter getBloomFilter(String redisKey) {
        return getBloomFilter(redisKey, 10_000_000, 0.000001);
    }

    /**
     * 根据Key值返回一个生成好的布隆过滤器
     *
     * @param redisKey               Redis的Key
     * @param elementsCount          数据量
     * @param falsePositiveProbality 容错率
     * @return
     */
    public BloomFilter getBloomFilter(String redisKey, Integer elementsCount, double falsePositiveProbality) {

        String key = dupPrefix + redisKey;

        if (mapCache.containsKey(key)) {
            return mapCache.get(key);
        } else {

            // 实例化，否则会报Null
            redisService = new RedisTemplateUtil(redisProperties);

            BloomFilter bloomFilterRedis = BloomConnectUtil.buildBloomFilter(redisProperties, elementsCount, falsePositiveProbality, key);
            mapCache.putIfAbsent(key, bloomFilterRedis);
            return bloomFilterRedis;
        }
    }

}
