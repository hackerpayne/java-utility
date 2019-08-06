package com.lingdonge.auth.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingdonge.auth.configuration.properties.ShiroProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.io.Serializable;

@Configuration
@EnableConfigurationProperties(ShiroProperties.class) // 开启指定类的配置
@ConditionalOnProperty(name = "auth.redis.enabled")// 必须开启shiro.set才会使用此配置
@Slf4j
public class ShiroRedisConfiguration {

    /**
     * 加载属性文件数据
     *
     * @return
     */
    @Bean(name = "shiroProperties")
    public ShiroProperties shiroProperties() {
        return new ShiroProperties();
    }

    /**
     * shiro的RedisTemplate注入
     *
     * @return
     */
    @Bean(name = "shiroRedisTemplate")
    public RedisTemplate<Serializable, Object> shiroRedisTemplate() {
        RedisTemplate<Serializable, Object> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);// 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper
        jackson2JsonRedisSerializer.setObjectMapper(om);
        template.setConnectionFactory(connectionFactory());
        template.setValueSerializer(jackson2JsonRedisSerializer);//如果key是String 需要配置一下StringSerializer,不然key会乱码 /XX/XX
        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis连接客户端(Shiro缓存管理)
     *
     * @return
     */
    @Primary
    @Bean(name = "connectionFactory")
    @DependsOn(value = "shiroProperties")
    public RedisConnectionFactory connectionFactory() {
        JedisConnectionFactory conn = new JedisConnectionFactory();
        conn.setDatabase(shiroProperties().getDatabaseShiro());
        conn.setHostName(shiroProperties().getHost());
        conn.setPassword(StringUtils.isBlank(shiroProperties().getPass()) ? null : shiroProperties().getPass());
        conn.setPort(shiroProperties().getPort());
        conn.setTimeout(shiroProperties().getTimeout());

        JedisPoolConfig config = new JedisPoolConfig();
        //连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        config.setBlockWhenExhausted(true);
        //设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
        config.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");
        //是否启用pool的jmx管理功能, 默认true
        config.setJmxEnabled(true);
        config.setJmxNamePrefix("pool");
        //是否启用后进先出, 默认true
        config.setLifo(true);
        //最大空闲连接数, 默认8个
        config.setMaxIdle(10);
        //最大连接数, 默认8个
        config.setMaxTotal(50);
        //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        config.setMaxWaitMillis(-1);

        //逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        config.setMinEvictableIdleTimeMillis(1800000);

        //最小空闲连接数, 默认0
        config.setMinIdle(0);

        //每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        config.setNumTestsPerEvictionRun(3);

        //对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
        config.setSoftMinEvictableIdleTimeMillis(1800000);

        //在获取连接的时候检查有效性, 默认false
        config.setTestOnBorrow(false);

        //在空闲时检查有效性, 默认false
        config.setTestWhileIdle(false);

        //逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        config.setTimeBetweenEvictionRunsMillis(-1);

        conn.setPoolConfig(config);

        return conn;
    }


    /**
     * 用户授权信息缓存
     * @return
     */
//    @Bean
//    public CacheManager cacheManager() {
//        // EhCacheManager cacheManager = new EhCacheManager();
//        // cacheManager.setCacheManagerConfigFile("classpath:ehcache.xml");
//        return new MemoryConstrainedCacheManager();
//    }

    /**
     * 配置shiro redisManager
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(shiroProperties().getHost());
        redisManager.setPort(shiroProperties().getPort());
        redisManager.setPassword(StringUtils.isBlank(shiroProperties().getPass()) ? null : shiroProperties().getPass());
        redisManager.setExpire(shiroProperties().getExpire());//配置缓存过期时间
        redisManager.setTimeout(shiroProperties().getTimeout());
        log.info("<<<<<<<<<<<<<<< 初始化Shiro >>>>>>>>>>>>>>>>>>");
        return redisManager;
    }

    /**
     * cacheManager 缓存 redis实现
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean()
    public RedisCacheManager getRedisCacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        return redisCacheManager;
    }
}
