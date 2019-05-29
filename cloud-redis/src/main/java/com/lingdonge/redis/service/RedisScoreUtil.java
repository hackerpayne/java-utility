package com.lingdonge.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import redis.clients.jedis.Tuple;

import java.util.Set;

/**
 * 根据Redis计算得分与占比
 */
@Slf4j
public class RedisScoreUtil {

    private RedisPoolUtil redisPoolUtil;

    /**
     * 构造函数
     *
     * @param redisProperties
     */
    public RedisScoreUtil(RedisProperties redisProperties) {
        this.redisPoolUtil = new RedisPoolUtil(redisProperties); // 创建连接工具
    }

    /**
     * 添加一条数据，并录入分数
     *
     * @param redisKey
     * @param item
     * @param score
     */
    public void addScore(String redisKey, String item, double score) {
        this.redisPoolUtil.zadd(redisKey, score, item);
    }

    /**
     * 当需要计算总分时，需要遍历分页去计算 ，非常非常麻烦。
     * 最好还是插入的时候，另起一个str字段去求和计算score的分数。否则需要遍历整个zscore
     *
     * @param redisKey
     * @return
     */
    public Long getTotalScore(String redisKey) {

        Long totalScore = 0l;
        final long[] scores = new long[]{0}; // 定义成数组，便于给Lamda使用。

        // 粗暴方式一：遍历所有，可能会内存崩溃掉
//        Set<Tuple> listDatas = this.redisPoolUtil.zrangeAllWithScore(redisKey);
//        listDatas.forEach(item -> scores[0] += item.getScore());
//        totalScore = scores[0];

        // 遍历方式二：分页遍历，推荐使用，如果量大的话
        long totalCount = this.redisPoolUtil.zcard(redisKey);
        log.debug("数据总数为：" + totalCount);
        int perPage = 100;
        log.debug("一共有多少页：" + totalCount / perPage);
        for (int pageNumber = 1; pageNumber <= totalCount / perPage + 1; pageNumber++) {
            long start = totalCount - ((pageNumber - 1) * perPage + 1);
            long end = start - perPage;
            System.out.println(" ===== Printing page " + (pageNumber));
            Set<Tuple> values = this.redisPoolUtil.zrevrangeByScoreWithScore(redisKey, start, end + 1);
            values.forEach(item -> scores[0] += item.getScore()); // 表达式求和
        }
        return totalScore;
    }

    /**
     * 计算 元素总个数
     *
     * @param redisKey
     * @return
     */
    public Long getTotalCount(String redisKey) {
        return this.redisPoolUtil.zcard(redisKey);
    }

}
