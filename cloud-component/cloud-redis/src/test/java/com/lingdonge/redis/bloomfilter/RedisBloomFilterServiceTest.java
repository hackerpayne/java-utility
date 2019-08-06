package com.lingdonge.redis.bloomfilter;

import com.lingdonge.core.dates.JodaUtil;
import lombok.extern.slf4j.Slf4j;
import orestes.bloomfilter.BloomFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
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

        log.info("Start At：{}", JodaUtil.getTime());
        for (Integer i = 0; i < 10000; i++) {
            bloomFilterRedis.add(String.valueOf(i));
        }

        log.info("Done:{}", JodaUtil.getTime());
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
