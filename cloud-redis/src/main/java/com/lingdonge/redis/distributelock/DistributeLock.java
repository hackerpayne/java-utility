package com.lingdonge.redis.distributelock;

/**
 * Redis分布式锁
 */
public interface DistributeLock {
    /**
     * 默认锁有效时间(单位毫秒)，默认60秒有效期
     */
    public static final long DEFAULT_LOCK_EXPIRE_TIME_MS = 60 * 1000;
    /**
     * 默认睡眠时间(单位毫秒)
     */
    public static final long DEFAULT_SLEEP_TIME_MS = 100;

    /**
     * 尝试锁
     *
     * @param lock             锁的键
     * @param requestTimeoutMS 请求超时 ms
     * @return 如果锁成功，则返回true；否则返回false
     */
    boolean tryLock(String lock, long requestTimeoutMS);

    /**
     * 尝试锁
     *
     * @param lock             锁的键
     * @param lockExpireTimeMS 锁有效期 ms
     * @param requestTimeoutMS 请求超时 ms
     */
    boolean tryLock(String lock, long lockExpireTimeMS, long requestTimeoutMS);

    /**
     * 解锁
     *
     * @param lock 锁的键
     */
    void unlock(String lock);
}