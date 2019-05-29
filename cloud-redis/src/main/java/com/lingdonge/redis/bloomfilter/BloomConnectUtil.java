package com.lingdonge.redis.bloomfilter;

import com.lindonge.core.reflect.OptionalUtil;
import orestes.bloomfilter.BloomFilter;
import orestes.bloomfilter.FilterBuilder;
import orestes.bloomfilter.HashProvider;
import orestes.bloomfilter.redis.helper.RedisPool;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.time.Duration;
import java.util.HashSet;

public class BloomConnectUtil {

    /**
     * 创建RedisBloomFilter，默认1千万数据
     *
     * @param redisProperties
     * @param redisKey
     * @return
     */
    public static BloomFilter buildBloomFilter(RedisProperties redisProperties, String redisKey) {
        return buildBloomFilter(redisProperties, 10_000_000, 0.000001, redisKey);
    }

    /**
     * 创建RedisBloomFilter
     *
     * @param redisProperties        Redis配置
     * @param elementsCount          数据量，例 10_000_000
     * @param falsePositiveProbality 容错率，例 0.000001
     * @param redisKey               Redis的主键名
     * @return
     */
    public static BloomFilter buildBloomFilter(RedisProperties redisProperties, Integer elementsCount, double falsePositiveProbality, String redisKey) {

        Integer connections = 10;
        if (OptionalUtil.resolve(() -> redisProperties.getJedis().getPool().getMaxActive()).isPresent()) {
            connections = redisProperties.getJedis().getPool().getMaxActive();
        }
        if (OptionalUtil.resolve(() -> redisProperties.getLettuce().getPool().getMaxActive()).isPresent()) {
            connections = redisProperties.getJedis().getPool().getMaxActive();
        }

        if (redisProperties.getTimeout() == null) {
            redisProperties.setTimeout(Duration.ofSeconds(10));
        }
        if (StringUtils.isEmpty(redisProperties.getHost())) {
            redisProperties.setHost("localhost");
        }

        if (StringUtils.isEmpty(redisProperties.getPassword())) {
            redisProperties.setPassword(null);
        }

        Long timeout = redisProperties.getTimeout().getSeconds() * 1000;

        RedisPool redisPool = null;
        if (redisProperties.getSentinel() != null) {
            redisPool = RedisPool.sentinelBuilder()
                    .host(redisProperties.getHost())
                    .port(redisProperties.getPort())
                    .password(redisProperties.getPassword())
                    .master(redisProperties.getSentinel().getMaster())
                    .sentinels(new HashSet<>(redisProperties.getSentinel().getNodes()))
                    .database(redisProperties.getDatabase())
                    .redisConnections(connections)
                    .timeout(timeout.intValue())
                    .build();

        } else {
            redisPool = RedisPool.builder()
                    .host(redisProperties.getHost())
                    .port(redisProperties.getPort())
                    .password(redisProperties.getPassword())
                    .database(redisProperties.getDatabase())
                    .redisConnections(connections)
                    .timeout(timeout.intValue())
                    .build();
        }

        BloomFilter bloomFilter = new FilterBuilder(elementsCount, falsePositiveProbality)
                .hashFunction(HashProvider.HashMethod.Murmur3)
                .name(redisKey)
                .redisBacked(true) // 开启Redis模式，使用Redis的话，必须开启这个
                .redisHost(redisProperties.getHost())
                .redisPort(redisProperties.getPort())
                .password(redisProperties.getPassword())
                .database(redisProperties.getDatabase())
                .redisConnections(connections)
//                .overwriteIfExists(false) // 是否覆盖已有数据
                .pool(redisPool)
                .complete()
                .buildBloomFilter();

        return bloomFilter;
    }
}
