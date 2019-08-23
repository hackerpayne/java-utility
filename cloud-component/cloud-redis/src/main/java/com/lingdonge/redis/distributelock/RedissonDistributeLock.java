package com.lingdonge.redis.distributelock;

import com.lingdonge.redis.util.RedissonUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson实现的分布式锁
 */
@Slf4j
public class RedissonDistributeLock extends AbstractDistributedLock {

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

    /**
     * 加锁
     *
     * @param key         RedisKey值
     * @param expire      过期时间
     * @param retryTimes  重试次数
     * @param sleepMillis 失败休眠时间
     * @return
     */
    @Override
    public boolean lock(String key, long expire, int retryTimes, long sleepMillis) {

        try {
            RLock lock = redissonClient.getLock(key);

//            lock.lock(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS); // 自定义释放时间
            boolean result = lock.tryLock(sleepMillis, expire, TimeUnit.MILLISECONDS); // 加锁

            // 如果获取锁失败，按照传入的重试次数进行重试
            while ((!result) && retryTimes-- > 0) {
                try {
                    log.debug("lock failed, retrying..." + retryTimes);
                    Thread.sleep(sleepMillis);
                } catch (InterruptedException e) {
                    return false;
                }
                result = lock.tryLock(sleepMillis, expire, TimeUnit.MILLISECONDS); // 加锁
            }
            return result;
        } catch (InterruptedException e) {
            return false;
        }

    }

    /**
     * 解锁
     *
     * @param lockKey
     */
    @Override
    public boolean releaseLock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.unlock();
        return true;
    }

}
