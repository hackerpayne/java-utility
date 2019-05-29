package com.lingdonge.redis.distributelock;

import java.util.List;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisLockService {

//    private Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String lock(String lockName) {
        return lockWithTimeout(lockName, 3000L, 3000L);
    }

    /**
     * 加锁
     *
     * @param key   productId - 商品的唯一标志
     * @param value 当前时间+超时时间 也就是时间戳
     * @return
     */
    public boolean lock(String key, String value) {
        if (stringRedisTemplate.opsForValue().setIfAbsent(key, value)) {//对应setnx命令
            //可以成功设置,也就是key不存在
            return true;
        }

        //判断锁超时 - 防止原来的操作异常，没有运行解锁操作  防止死锁
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        //如果锁过期
        if (!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {//currentValue不为空且小于当前时间
            //获取上一个锁的时间value
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);//对应getset，如果key存在

            //假设两个线程同时进来这里，因为key被占用了，而且锁过期了。获取的值currentValue=A(get取的旧的值肯定是一样的),两个线程的value都是B,key都是K.锁时间已经过期了。
            //而这里面的getAndSet一次只会一个执行，也就是一个执行之后，上一个的value已经变成了B。只有一个线程获取的上一个值会是A，另一个线程拿到的值是B。
            if (!StringUtils.isEmpty(oldValue) && oldValue.equals(currentValue)) {
                //oldValue不为空且oldValue等于currentValue，也就是校验是不是上个对应的商品时间戳，也是防止并发
                return true;
            }
        }
        return false;
    }


    /**
     * 解锁
     *
     * @param key
     * @param value
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                stringRedisTemplate.opsForValue().getOperations().delete(key);//删除key
            }
        } catch (Exception e) {
            log.error("[Redis分布式锁] 解锁出现异常了，{}", e);
        }
    }

    /**
     * 获取锁
     *
     * @param locaName
     * @param acquireTimeout
     * @param timeout
     * @return
     */
    public String lockWithTimeout(String locaName, long acquireTimeout, long timeout) {
        String retIdentifier = null;
        RedisConnectionFactory connectionFactory = stringRedisTemplate.getConnectionFactory();
        RedisConnection redisConnection = connectionFactory.getConnection();
        // 获取连接
        // 随机生成一个value
        String identifier = UUID.randomUUID().toString();
        // 锁名，即key值
        String lockKey = "lock:" + locaName;
        // 超时时间，上锁后超过此时间则自动释放锁
        int lockExpire = (int) (timeout / 1000);
        // 获取锁的超时时间，超过这个时间则放弃获取锁
        long end = System.currentTimeMillis() + acquireTimeout;
        while (System.currentTimeMillis() < end) {
            if (redisConnection.setNX(lockKey.getBytes(), identifier.getBytes())) {
                redisConnection.expire(lockKey.getBytes(), lockExpire);
                // 返回value值，用于释放锁时间确认
                retIdentifier = identifier;
                RedisConnectionUtils.releaseConnection(redisConnection, connectionFactory);
                return retIdentifier;
            }
            // 返回-1代表key没有设置超时时间，为key设置一个超时时间
            if (redisConnection.ttl(lockKey.getBytes()) == -1) {
                redisConnection.expire(lockKey.getBytes(), lockExpire);
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                log.warn("获取到分布式锁：线程中断！");
                Thread.currentThread().interrupt();
            }
        }
        RedisConnectionUtils.releaseConnection(redisConnection, connectionFactory);//释放链接，避免死锁
        return retIdentifier;
    }

    /**
     * 释放锁
     *
     * @param lockName   锁的key
     * @param identifier 释放锁的标识
     * @return
     */
    public boolean releaseLock(String lockName, String identifier) {
        if (identifier == null || "".equals(identifier)) {
            return false;
        }
        RedisConnectionFactory connectionFactory = stringRedisTemplate.getConnectionFactory();
        RedisConnection redisConnection = connectionFactory.getConnection();
        String lockKey = "lock:" + lockName;
        boolean releaseFlag = false;
        while (true) {
            try {
                // 监视lock，准备开始事务
                redisConnection.watch(lockKey.getBytes());
                // 通过前面返回的value值判断是不是该锁，若是该锁，则删除，释放锁
                byte[] valueBytes = redisConnection.get(lockKey.getBytes());
                if (valueBytes == null) {
                    redisConnection.unwatch();
                    releaseFlag = false;
                    break;
                }
                String identifierValue = new String(valueBytes);
                if (identifier.equals(identifierValue)) {
                    redisConnection.multi();
                    redisConnection.del(lockKey.getBytes());
                    List<Object> results = redisConnection.exec();
                    if (results == null) {
                        continue;
                    }
                    releaseFlag = true;
                }
                redisConnection.unwatch();
                break;
            } catch (Exception e) {
                log.warn("释放锁异常", e);
                e.printStackTrace();
            }
        }
        RedisConnectionUtils.releaseConnection(redisConnection, connectionFactory);
        return releaseFlag;
    }


}
