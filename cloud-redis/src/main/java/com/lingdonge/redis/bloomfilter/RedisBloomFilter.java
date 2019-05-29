package com.lingdonge.redis.bloomfilter;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * 基于Redis的BloomFilter过滤器
 * <p>
 * 512MB可以放下2亿左右的数据
 * <p>
 * 代码来源 https://github.com/olylakers/RedisBloomFilter
 * <p>
 * 唯一需要注意的是redis的bitmap只支持2^32大小，对应到内存也就是512MB,数组的下标最大只能是2^32-1。不过这个限制我们可以通过构建多个redis的bitmap通过hash取模的方式分散一下即可。
 */
public class RedisBloomFilter {

    private String hosts;
    private int timeout;
    private int maxKey;
    private float errorRate;
    private int hashFunctionCount;

    private int bitSize;

    private ShardedJedisPool pool;

    private String defaultKey = "redis:bloomfilter";

    private static final String hostConfig = "127.0.0.1:6001";

    /**
     * @param hosts     Redis主机列表
     * @param timeout   连接超时时间
     * @param errorRate 容错率
     * @param maxKey    期望放入的元素最大个数
     */
    public RedisBloomFilter(String hosts, int timeout, float errorRate, int maxKey) {
        this.hosts = hosts;
        this.timeout = timeout;
        this.maxKey = maxKey;
        this.errorRate = errorRate;
        String[] hostInfos = hosts.split(";");
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        if (StringUtils.isNotBlank(hosts)) {
            for (String hostInfo : hostInfos) {
                String[] host = hostInfo.split(":");
                if (host.length != 2) {
                    throw new IllegalArgumentException("hosts should not be null or empty");
                }
                int port = 0;
                if (NumberUtils.isNumber(host[1])) {
                    port = NumberUtils.toInt(host[1]);
                }
                shards.add(new JedisShardInfo(host[0], port, timeout));
            }
        } else {
            throw new IllegalArgumentException("redis host.length != 2");
        }

        pool = initRedisPool(shards);

        bitSize = calcOptimalM(maxKey, errorRate);

        hashFunctionCount = calcOptimalK(bitSize, maxKey);
    }

    /**
     * 初始化Redis集群
     *
     * @param shards
     * @return
     */
    private ShardedJedisPool initRedisPool(List<JedisShardInfo> shards) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setTestOnBorrow(false);
        config.setTestOnReturn(false);
        config.setMinIdle(8);
        config.setMaxIdle(50);
        config.setMaxTotal(50);
        config.setMaxWaitMillis(2 * 1000);
        config.setMinEvictableIdleTimeMillis(1000L * 60L * 60L * 5L);//即一个redis连接的空闲时间超过5个小时才会被connection pool给回收

//        //在借出的时候不测试有效性
//        configuration.testOnBorrow = false;
//        //在还回的时候不测试有效性
//        configuration.testOnReturn = false;
//        //最小空闲数
//        configuration.minIdle = 8;
//        //最大空闲数
//        configuration.maxIdle = 50;
//        //最大连接数
//        configuration.maxActive = 50;
//        //允许最大等待时间，2s，单位：ms
//        configuration.maxWait = 2 * 1000;
//
//        configuration.minEvictableIdleTimeMillis = 1000L * 60L * 60L * 5L;

        return new ShardedJedisPool(config, shards);
    }

    /**
     * add one object, using default key
     *
     * @param bizId
     */
    public void add(int bizId) {
        add(defaultKey, bizId);
    }

    /**
     * add one object using the specified key
     *
     * @param key   Redis的键名
     * @param bizId
     */
    public void add(String key, long bizId) {
        int[] offset = HashUtils.murmurHashOffset(bizId, hashFunctionCount, bitSize);
        ShardedJedis jedis = null;
        boolean connected = true;
        try {
            jedis = pool.getResource();
            for (int i : offset) {
                jedis.setbit(key, i, true);
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * add one object using the specified key
     *
     * @param key
     * @param bizId
     */
    public void addWithPipe(String key, long bizId) {
        int[] offset = HashUtils.murmurHashOffset(bizId, hashFunctionCount, bitSize);
        ShardedJedis jedis = null;
        boolean connected = true;
        try {
            jedis = pool.getResource();
            ShardedJedisPipeline pipeline = jedis.pipelined();
            for (int i : offset) {
                pipeline.setbit(key, i, true);
            }

            pipeline.sync();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * Check if a bizId is part of the set
     *
     * @param key
     * @param bizId
     */
    public boolean include(String key, long bizId) {
        int[] offset = HashUtils.murmurHashOffset(bizId, hashFunctionCount, bitSize);
        ShardedJedis jedis = null;
        boolean connected = true;
        try {
            jedis = pool.getResource();
            for (int i : offset) {
                if (!jedis.getbit(key, i)) {
                    return false;
                }
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return true;
    }

    /**
     * Check if a bizId is part of the set
     *
     * @param key
     * @param bizId
     */
    public boolean includeWithPipe(String key, long bizId) {
        int[] offset = HashUtils.murmurHashOffset(bizId, hashFunctionCount, bitSize);
        ShardedJedis jedis = null;
        boolean connected = true;
        try {
            jedis = pool.getResource();
            ShardedJedisPipeline pipeline = jedis.pipelined();
            for (int i : offset) {
                pipeline.getbit(key, i);
            }

            List<Object> responses = pipeline.syncAndReturnAll();
            for (Object object : responses) {
                if (object instanceof Boolean) {
                    Boolean contains = (Boolean) object;
                    if (!contains) {
                        return false;
                    }
                }
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return true;
    }

    /**
     * @param key
     * @return
     */
    public long count(String key) {
        ShardedJedis jedis = null;
        boolean connected = true;
        try {
            jedis = pool.getResource();
            return jedis.bitcount(key);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * @param key
     * @return
     */
    public String getRedisData(String key) {
        ShardedJedis jedis = null;
        boolean connected = true;
        try {
            jedis = pool.getResource();
            String redisData = jedis.get(key);
            return redisData;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 计算bloomFilter的max bit size
     * Calculate M and K
     * See http://en.wikipedia.org/wiki/Bloom_filter#Probability_of_false_positives for more details
     *
     * @param maxKey    期望放入的元素最大个数
     * @param errorRate 容错率
     * @return
     */
    public int calcOptimalM(int maxKey, float errorRate) {
        return (int) Math.ceil(maxKey
                * (Math.log(errorRate) / Math.log(0.6185)));
    }

    /**
     * 计算bloomFilter的k
     * Calculate M and K
     * See http://en.wikipedia.org/wiki/Bloom_filter#Probability_of_false_positives for more details
     *
     * @param bitSize bloomfilter的bits
     * @param maxKey  期望放入的元素最大个数
     * @return
     */
    public int calcOptimalK(int bitSize, int maxKey) {
        return (int) Math.ceil(Math.log(2) * (bitSize / maxKey));
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxKey() {
        return maxKey;
    }

    public void setMaxKey(int maxKey) {
        this.maxKey = maxKey;
    }

    public float getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(float errorRate) {
        this.errorRate = errorRate;
    }

    public int getHashFunctionCount() {
        return hashFunctionCount;
    }

    public void setHashFunctionCount(int hashFunctionCount) {
        this.hashFunctionCount = hashFunctionCount;
    }

    public int getBitSize() {
        return bitSize;
    }

    public void setBitSize(int bitSize) {
        this.bitSize = bitSize;
    }

    /**
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        // TODO Auto-generated method stub

        RedisBloomFilter bloomFilter = new RedisBloomFilter(hostConfig, 1000, 0.00000001f, (int) Math.pow(2, 31));
        System.out.println(bloomFilter.getBitSize() / 8 / 1024 / 2014);
    }

}