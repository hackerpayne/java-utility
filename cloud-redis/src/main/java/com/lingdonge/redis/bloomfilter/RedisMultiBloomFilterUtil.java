package com.lingdonge.redis.bloomfilter;

import com.google.common.collect.Maps;
import com.lindonge.core.dates.DatePattern;
import com.lindonge.core.dates.LocalDateUtil;
import com.lingdonge.redis.service.RedisTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import orestes.bloomfilter.BloomFilter;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * 多天同时过滤器
 * 应用场景：3天不重复，5天数据不重复策略
 */
@Slf4j
public class RedisMultiBloomFilterUtil {

    /**
     * 布隆过滤器列表
     */
    private ConcurrentMap<String, BloomFilter> mapCache = Maps.newConcurrentMap();

    private RedisTemplateUtil redisTemplateUtil;

    private String redisPrefix = "";

    private RedisProperties redisProperties;

    private Integer holdDays = 7;

    /**
     * 传入配置，指定过期策略
     *
     * @param redisProperties
     * @param holdDays        默认使用多少天的布隆过滤器，超过期限的，会自动删掉
     */
    public RedisMultiBloomFilterUtil(RedisProperties redisProperties, Integer holdDays) {
        this.redisPrefix = holdDays + "";
        this.redisProperties = redisProperties;
        this.holdDays = holdDays;
    }

    /**
     * 可以对满足条件的URL移动到队列左侧
     *
     * @param redisProperties Redis连接配置信息
     * @param redisPrefix     设置Redis的前缀值
     * @param holdDays        默认使用多少天的布隆过滤器，超过期限的，会自动删掉
     */
    public RedisMultiBloomFilterUtil(RedisProperties redisProperties, String redisPrefix, Integer holdDays) {
        this.redisPrefix = redisPrefix;
        this.redisProperties = redisProperties;
        this.holdDays = holdDays;
    }

    /**
     * 根据日期生成那一天的布隆过滤器
     *
     * @param localDate
     * @return
     */
    private String getRedisKey(LocalDate localDate) {
        String dates = LocalDateUtil.getDate(localDate, DatePattern.DATE_SIMPLE_FORMAT);
        return this.redisPrefix + "_bmultifilter_" + dates;
    }

    /**
     * 根据日期创建当天的布隆过滤器
     *
     * @param localDate
     * @return
     */
    private BloomFilter initByDates(LocalDate localDate) {

        String dates = LocalDateUtil.getDate(localDate, DatePattern.DATE_SIMPLE_FORMAT);
        String redisKey = getRedisKey(localDate);

        BloomFilter bloomFilter = BloomConnectUtil.buildBloomFilter(redisProperties, redisKey);
        mapCache.put(dates, bloomFilter);
//        expireBloom(redisKey); // 强制设置过期逻辑
        return bloomFilter;
    }

    /**
     * 设定指定日期的布隆过滤器，在多少天后过期
     * 防止数据堆积
     *
     * @param redisKey
     */
    private void expireBloom(String redisKey) {
        if (redisTemplateUtil == null) {
            redisTemplateUtil = new RedisTemplateUtil(redisProperties);
        }
        redisTemplateUtil.expire(redisKey, (this.holdDays + 2) * 24 * 60 * 60); // 以天为单位设置过期时间，多预留2天过期值
    }

    /**
     * 判断是否重复
     *
     * @param input
     * @return
     */
    public boolean contains(String input) {

        // 得到近几天的日期
        List<LocalDate> latestBlooms = LocalDateUtil.getDatesBetweenUsingJava8(LocalDate.now().minusDays(this.holdDays), LocalDate.now());

        boolean contains = false;// 默认不包含
        for (LocalDate localDate : latestBlooms) {
            String mapKey = LocalDateUtil.getDate(localDate, DatePattern.DATE_SIMPLE_FORMAT);

            // 没有对象就传到列表里面构造
            if (!mapCache.containsKey(mapKey)) {
                initByDates(localDate);
            }

            BloomFilter redisBloomFilter = mapCache.get(mapKey);
            if (redisBloomFilter != null && redisBloomFilter.contains(input + "")) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * 添加到过滤器，默认添加到今天的过滤器内
     *
     * @param input
     * @return
     */
    public boolean add(String input) {

        LocalDate localDate = LocalDate.now();

        String mapKey = LocalDateUtil.getDate(localDate, DatePattern.DATE_SIMPLE_FORMAT);
        if (!mapCache.containsKey(mapKey)) {
            initByDates(localDate);
        }
        BloomFilter bloomFilterRedis = mapCache.get(mapKey);
        if (bloomFilterRedis != null) {
            return bloomFilterRedis.add(input + "");
        }
        return false;
    }

}
