package com.lingdonge.redis.distributelock;

import com.lingdonge.redis.util.RedissonUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson实现的分布式锁
 */
public class RedissonDistributeLock {

    private RedissonClient redissonClient;

    public RedissonDistributeLock() {
    }

    /**
     * 可以通过配置来固定设置
     *
     * @param redisProperties
     */
    public RedissonDistributeLock(RedisProperties redisProperties) {
        redissonClient = RedissonUtil.getRedissonClient(redisProperties);
    }

    public void testTryLock() {
        RLock redLock = redissonClient.getLock("REDLOCK_KEY");
        Boolean isLock = false;
        try {
            isLock = redLock.tryLock();

            // 500ms拿不到锁, 就认为获取锁失败。10000ms即10s是锁失效时间。
            isLock = redLock.tryLock(500, 10000, TimeUnit.MILLISECONDS);

            if (isLock) { // 如果加锁成功，执行业务逻辑

            }
        } catch (Exception ex) {
        } finally {
            // 无论如何，最后需要解锁
            redLock.unlock();
        }
    }

    /**
     * 加锁
     *
     * @param lockKey
     * @return
     */
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /**
     * 加锁，带释放时间
     *
     * @param lockKey   加锁的Key
     * @param leaseTime 释放时间，单位秒
     * @return
     */
    public RLock lock(String lockKey, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(leaseTime, TimeUnit.SECONDS);
        return lock;
    }

    /**
     * 加锁，自定义释放时间
     *
     * @param lockKey
     * @param unit
     * @param timeout
     * @return
     */
    public RLock lock(String lockKey, TimeUnit unit, int timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, unit);
        return lock;
    }

    /**
     * 加锁
     *
     * @param lockKey
     * @param unit
     * @param waitTime
     * @param leaseTime
     * @return
     */
    public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            return false;
        }
    }

    /**
     * 解锁
     *
     * @param lockKey
     */
    public void unlock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
    }

    /**
     * 解锁
     *
     * @param lock
     */
    public void unlock(RLock lock) {
        lock.unlock();
    }

    /**
     * 设置连接器
     *
     * @param redissonClient
     */
    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

}
