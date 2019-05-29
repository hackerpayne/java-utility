package com.lingdonge.redis.distributelock;

import com.google.common.base.Preconditions;
import com.lindonge.core.dates.SystemClock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.TimeUnit;

/**
 * Description:分布式锁
 * 代码来源：https://my.oschina.net/u/2313177/blog/1590423
 */
@Slf4j
public class RedisDistributeLock implements DistributeLock {

//    private static final Logger log = LoggerFactory.getLogger(RedisDistributeLock.class);

    /**
     * 每秒的毫秒数
     */
    private static final long MILLIS_PER_SECOND = 1000L;
    /**
     * 0值
     */
    private static final int INT_ZERO = 0;
    /**
     * 1值
     */
    private static final int INT_ONE = 1;
    /**
     * jedisCluster
     */
    @Autowired
    private JedisCluster jedisCluster;

    @Override
    public boolean tryLock(String lock, long requestTimeoutMS) {
        return this.tryLock(lock, DEFAULT_LOCK_EXPIRE_TIME_MS, requestTimeoutMS);
    }

    /**
     * 上锁 流程：
     * 1：参数校验，对传入的互斥key，锁超时时间，请求超时时间进行检验
     * <p>
     * 2：在请求超时时间之内的请求，这里以while死循环的方式不断进行获取锁重试
     * <p>
     * 3：设置锁过期时间，并尝试用setnx命令，redis之前不存在key的情况下，设置key，同时把过期时间expire作为value设置进去。如果获取成功，这里给锁加上真正的过期时间，获取锁成功~
     * <p>
     * 4：在第三步没有成功的情况下，我们直接再次获取锁。如果为空，则说明锁已经过期，或者已经被其他线程解锁，那么我们立刻结束本次循环，尝试重新获取~
     * <p>
     * 5：如果第四步获取锁成功，我们需要进行一下判断：1拿到锁的过期时间（key对应的value），并判断锁是否在过期时间之内，如果在的话，用具有原子性操作的命令getset，取出之前的过期时间oldValue值，这里会有两种情况：
     * <p>
     * //1.如果拿到的旧值是空则说明在此线程做getSet之前已经有线程将锁删除，由于此线程getSet操作之后已经对锁设置了值，实际上相当于它已经占有了锁
     * <p>
     * //2.如果拿到的旧值不为空且等于前面查到的值，则说明在此线程进行getSet操作之前没有其他线程对锁设置了值,则此线程是第一个占有锁的
     * 两种情况都说明已经获取锁成功，结束循环
     * <p>
     * 以上步骤都是建立在，请求超时时间之内的，这里每次循环获取，间隔100毫秒~，当获取时间超过请求超时时间的话：也是锁获取失败的一种情况
     *
     * @param lock             锁的键
     * @param lockExpireTimeMS 锁有效期 ms
     * @param requestTimeoutMS 请求超时 ms
     * @return
     */
    @Override
    public boolean tryLock(String lock, long lockExpireTimeMS, long requestTimeoutMS) {
        Preconditions.checkArgument(StringUtils.isNotBlank(lock), "lock invalid");
        Preconditions.checkArgument(lockExpireTimeMS > INT_ZERO, "lockExpireTimeMS invalid");
        Preconditions.checkArgument(requestTimeoutMS > INT_ZERO, "requestTimeoutMS invalid");

        while (requestTimeoutMS > INT_ZERO) {
            String expire = String.valueOf(SystemClock.now() + lockExpireTimeMS + INT_ONE);

            Long result = jedisCluster.setnx(lock, expire);
            if (result > INT_ZERO) {
                //目前没有线程占用此锁
                jedisCluster.expire(lock, Long.valueOf(lockExpireTimeMS / MILLIS_PER_SECOND).intValue());
                return true;
            }
            String currentValue = jedisCluster.get(lock);
            if (currentValue == null) {
                //锁已经被其他线程删除马上重试获取锁
                continue;
            } else if (Long.parseLong(currentValue) < SystemClock.now()) {
                //此处判断出锁已经超过了其有效的存活时间
                String oldValue = jedisCluster.getSet(lock, expire);
                if (oldValue == null || oldValue.equals(currentValue)) {
                    //1.如果拿到的旧值是空则说明在此线程做getSet之前已经有线程将锁删除，由于此线程getSet操作之后已经对锁设置了值，实际上相当于它已经占有了锁
                    //2.如果拿到的旧值不为空且等于前面查到的值，则说明在此线程进行getSet操作之前没有其他线程对锁设置了值,则此线程是第一个占有锁的
                    jedisCluster.expire(lock, Long.valueOf(lockExpireTimeMS / MILLIS_PER_SECOND).intValue());
                    return true;
                }
            }
            long sleepTime;
            if (requestTimeoutMS > DEFAULT_SLEEP_TIME_MS) {
                sleepTime = DEFAULT_SLEEP_TIME_MS;
                requestTimeoutMS -= DEFAULT_SLEEP_TIME_MS;
            } else {
                sleepTime = requestTimeoutMS;
                requestTimeoutMS = INT_ZERO;
            }
            try {
                TimeUnit.MILLISECONDS.sleep(sleepTime);
            } catch (InterruptedException e) {
                log.warn("分布式锁线程休眠异常{}", lock, e);
            }
        }
        return false;
    }

    @Override
    public void unlock(String lock) {
        String value = jedisCluster.get(lock);
        if (null != value && Long.parseLong(value) > SystemClock.now()) {
            //如果锁还存在并且还在有效时间则进行删除
            jedisCluster.del(lock);
        }
    }
}