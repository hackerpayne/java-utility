package com.lingdonge.redis.service;

import com.lingdonge.redis.util.RedisConnUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 接口幂等性处理接口：
 * 1、使用时先getToken生成随机Token给前端
 * 2、前端提交接口时，校验Token，只做一次校验，使用checkToken
 */
public class RedisIdempotentUtil {

    private StringRedisTemplate stringRedisTemplate;

    /**
     * 默认1分钟有效期
     */
    private long tokenTimeout = 60 * 60;


    public RedisIdempotentUtil(RedisProperties redisProperties) {
        this(redisProperties, 60 * 60);
    }

    /**
     * @param redisProperties Redis配置文件
     * @param tokenTimeout    Token有效期，默认值1分钟
     */
    public RedisIdempotentUtil(RedisProperties redisProperties, long tokenTimeout) {
        this.stringRedisTemplate = RedisConnUtil.getStringRedisTemplate(redisProperties);
        this.tokenTimeout = tokenTimeout;
        this.afterPropertySet();//注入和生成实例
    }

    /**
     * 构造完之后执行。
     */
    public void afterPropertySet() {
    }


    public String getToken() {
        String token = "token" + UUID.randomUUID();
        setString(token, token, tokenTimeout);
        return token;
    }

    /**
     * 检查是否幂等
     *
     * @param tokenKey Token的RedisKey值
     * @return
     */
    public boolean checkToken(String tokenKey) {
        String tokenValue = getString(tokenKey);
        if (StringUtils.isEmpty(tokenValue)) {
            return false;
        }
        // 保证每个接口对应的token只能访问一次，保证接口幂等性问题
        delKey(tokenKey);
        return true;
    }


    public void setString(String key, Object data, Long timeout) {
        if (data instanceof String) {
            String value = (String) data;
            stringRedisTemplate.opsForValue().set(key, value);
        }
        if (timeout != null) {
            stringRedisTemplate.expire(key, timeout, TimeUnit.SECONDS);
        }
    }

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void delKey(String key) {
        stringRedisTemplate.delete(key);
    }

}
