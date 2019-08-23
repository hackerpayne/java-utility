package com.lingdonge.redis.distributelock;

/**
 * Redis分布式锁
 * 参考配置：https://my.oschina.net/dengfuwei/blog/1600681
 */
public interface DistributeLock {

    /**
     * 默认锁有效时间(单位毫秒)，默认60秒有效期
     */
    public static final long TIMEOUT_MILLIS = 60 * 1000;

    /**
     * 默认睡眠时间(单位毫秒)
     */
    public static final long SLEEP_MILLIS = 100;

    /**
     * 默认重试次数
     */
    public static final int RETRY_TIMES = Integer.MAX_VALUE;

    boolean lock(String key);

    boolean lock(String key, int retryTimes);

    boolean lock(String key, int retryTimes, long sleepMillis);

    /**
     * 尝试锁
     * @param key Redis的Key值
     * @param expire 过期时间
     * @return
     */
    boolean lock(String key, long expire);

    /**
     * 尝试加锁
     * @param key
     * @param expire
     * @param retryTimes
     * @return
     */
    boolean lock(String key, long expire, int retryTimes);

    boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    /**
     * 解锁
     *
     * @param key 锁的键名
     * @return
     */
    public boolean releaseLock(String key);

}