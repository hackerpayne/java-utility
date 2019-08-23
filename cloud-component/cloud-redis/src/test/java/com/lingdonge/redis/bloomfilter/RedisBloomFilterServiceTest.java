package com.lingdonge.redis.bloomfilter;

import com.lingdonge.core.dates.LocalDateUtil;
import lombok.extern.slf4j.Slf4j;
import orestes.bloomfilter.BloomFilter;
import org.junit.Test;

import javax.annotation.Resource;

@Slf4j
public class RedisBloomFilterServiceTest {


    @Resource
    private RedisBloomFilterService redisBloomFilterService;

    @Test
    public void clear() {
    }

    @Test
    public void contains() {
    }

    @Test
    public void add() {

        BloomFilter bloomFilterRedis = redisBloomFilterService.getBloomFilter("bf_hongmai_duplicate");

        log.info("Start At：{}", LocalDateUtil.getNowTime());
        for (Integer i = 0; i < 10000; i++) {
            bloomFilterRedis.add(String.valueOf(i));
        }

        log.info("Done:{}", LocalDateUtil.getNowTime());
    }

    @Test
    public void containsThenIncr() {
    }

    @Test
    public void containsThenIncrInMem() {
    }

    @Test
    public void notContainsThenAdd() {
    }

    @Test
    public void getDuplicateCount() {
    }

    @Test
    public void getDuplicateCountInMem() {
    }
}
