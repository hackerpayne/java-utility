package com.lingdonge.redis.bloomfilter;

import orestes.bloomfilter.BloomFilter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

/**
 * 手动配置使用RedisBloomFilter进行去重
 * RedisBloomFilterScheduler
 * 基于Redis的BloomFilter过滤器，可以过滤上亿的数据集
 * 队列在queue_**.com下面，Bloom做重复过滤在bloomfilter_下面，旧的set过滤已经被淘汰
 */
public class RedisBloomFilterUtils {

    private static BloomFilter bloomFilter;//Bloom过滤器

    /**
     * 可以对满足条件的URL移动到队列左侧
     *
     * @param redisProperties Redis连接配置信息
     * @param redisKey        设置Redis的Key值
     */
    public RedisBloomFilterUtils(RedisProperties redisProperties, String redisKey) {
        // Bloom在Redis里面的Key值
        String bloomRedisKey = "bfilter_" + redisKey;
        bloomFilter = BloomConnectUtil.buildBloomFilter(redisProperties, 10_000_000, 0.000001, bloomRedisKey);
    }

    /**
     * 清空队列里面的数据
     */
    public void clear() {
        if (bloomFilter != null) {
            bloomFilter.clear();// 清理掉BloomFIlter
        }
    }

    /**
     * 判断是否重复
     *
     * @param input
     * @return
     */
    public boolean contains(String input) {
        return bloomFilter.contains(input);
    }

    /**
     * 添加到列表里面
     *
     * @param input
     * @return
     */
    public boolean add(String input) {
        return bloomFilter.add(input);
    }

    /**
     * 判断是否重复，不重复添加到列表里面
     *
     * @return
     */
    public boolean isDuplicate(String input) {
        boolean contains = bloomFilter.contains(input);
        if (contains) {
            return true;
        }

        bloomFilter.add(input);
        return false;
    }

    /**
     * 获取已经存在的数据量
     *
     * @return
     */
    public int getCount() {
        return bloomFilter.getEstimatedPopulation().intValue();
    }

}
