package com.lingdonge.redis.aspect;

import com.lingdonge.redis.annotation.RedisLock;
import com.lingdonge.redis.configuration.DistributedLockAutoConfiguration;
import com.lingdonge.redis.distributelock.DistributeLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 切面
 */
@Aspect
@Configuration
@ConditionalOnClass(DistributeLock.class)
@AutoConfigureAfter(DistributedLockAutoConfiguration.class)
@Slf4j
public class DistributedLockAspectConfiguration {

    @Autowired
    private DistributeLock distributedLock;

    @Pointcut("@annotation(com.itopener.lock.redis.spring.boot.autoconfigure.annotations.RedisLock)")
    private void lockPoint() {

    }

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String key = redisLock.value();
        if (StringUtils.isEmpty(key)) {
            Object[] args = pjp.getArgs();
            key = Arrays.toString(args);
        }
        int retryTimes = redisLock.action().equals(RedisLock.LockFailAction.CONTINUE) ? redisLock.retryTimes() : 0;
        boolean lock = distributedLock.lock(key, redisLock.keepMills(), retryTimes, redisLock.sleepMills());
        if (!lock) {
            log.debug("get lock failed : " + key);
            return null;
        }

        //得到锁,执行方法，释放锁
        log.debug("get lock success : " + key);
        try {
            return pjp.proceed();
        } catch (Exception e) {
            log.error("execute locked method occured an exception", e);
        } finally {
            boolean releaseResult = distributedLock.releaseLock(key);
            log.debug("release lock : " + key + (releaseResult ? " success" : " failed"));
        }
        return null;
    }
}