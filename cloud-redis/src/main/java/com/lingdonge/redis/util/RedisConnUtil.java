package com.lingdonge.redis.util;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lingdonge.core.reflect.OptionalUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;


/**
 * Redis配置生成类
 */
public class RedisConnUtil {

    /**
     * 取出Cluster里面的节点
     *
     * @param redisProperties
     * @return
     */
    public static Set<HostAndPort> getClusterNodes(RedisProperties redisProperties) {
        Set<HostAndPort> nodeSet = new HashSet<>();
        for (String node : redisProperties.getCluster().getNodes()) {
            String[] split = node.split(":");
            nodeSet.add(new HostAndPort(split[0], Integer.valueOf(split[1])));
        }
        return nodeSet;
    }

    /**
     * @param redisProperties
     * @return
     */
    public static Set<RedisNode> getClusterRedisNodes(RedisProperties redisProperties) {
        Set<RedisNode> nodeSet = new HashSet<>();
        for (String node : redisProperties.getCluster().getNodes()) {
            String[] split = node.split(":");
            nodeSet.add(new RedisNode(split[0], Integer.valueOf(split[1])));
        }
        return nodeSet;
    }

    /**
     * 取出Sentinel哨兵里面的节点
     *
     * @param redisProperties
     * @return
     */
    public static Set<HostAndPort> getSentinelNodes(RedisProperties redisProperties) {
        Set<HostAndPort> nodeSet = new HashSet<>();
        for (String node : redisProperties.getSentinel().getNodes()) {
            String[] split = node.split(":");
            nodeSet.add(new HostAndPort(split[0], Integer.valueOf(split[1])));
        }
        return nodeSet;
    }

    /**
     * 取出Sentinel哨兵里面的节点
     *
     * @param redisProperties
     * @return
     */
    public static Set<RedisNode> getSentinelRedisNodes(RedisProperties redisProperties) {
        Set<RedisNode> nodeSet = new HashSet<>();
        for (String node : redisProperties.getSentinel().getNodes()) {
            String[] split = node.split(":");
            nodeSet.add(new RedisNode(split[0], Integer.valueOf(split[1])));
        }
        return nodeSet;
    }

    /**
     * Redis哨兵模式配置明细
     * 不要使用Bean，防止直接创建。这里根据配置文件来建立
     *
     * @return
     */
    public static RedisSentinelConfiguration buildRedisSentinelConfiguration(RedisProperties properties) {
        RedisSentinelConfiguration configuration = new RedisSentinelConfiguration();
        configuration.setSentinels(getSentinelRedisNodes(properties));
        configuration.setMaster(properties.getSentinel().getMaster());
        return configuration;
    }

    /**
     * Cluster集群配置生成
     *
     * @param redisProperties
     * @return
     */
    public static RedisClusterConfiguration buildRedisClusterConfiguration(RedisProperties redisProperties) {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        // 配置集群密码和节点
        redisClusterConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        redisClusterConfiguration.setClusterNodes(getClusterRedisNodes(redisProperties));
        redisClusterConfiguration.setMaxRedirects(redisProperties.getCluster().getMaxRedirects());

        return redisClusterConfiguration;
    }

    /**
     * 生成Lettuce的配置
     *
     * @param redisProperties
     * @return
     */
    public static GenericObjectPoolConfig getLettucePoolConfig(RedisProperties redisProperties) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();

        if (null != redisProperties.getLettuce() && null != redisProperties.getLettuce().getPool()) {
            genericObjectPoolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
            genericObjectPoolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
            genericObjectPoolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
            genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getLettuce().getPool().getMaxWait().toMillis());
        } else {
            RedisProperties.Pool pool = new RedisProperties.Pool();
            genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
            genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
            genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
            genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }

        return genericObjectPoolConfig;
    }

    /**
     * 生成Jedis的 GenericObjectPoolConfig 配置
     *
     * @param redisProperties
     * @return
     */
    public static GenericObjectPoolConfig getJedisPoolGenericConfig(RedisProperties redisProperties) {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        if (null != redisProperties.getJedis() && null != redisProperties.getJedis().getPool()) {
            genericObjectPoolConfig.setMaxIdle(redisProperties.getJedis().getPool().getMaxIdle());
            genericObjectPoolConfig.setMinIdle(redisProperties.getJedis().getPool().getMinIdle());
            genericObjectPoolConfig.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
            genericObjectPoolConfig.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
        } else { // 没有配置使用默认设置
            RedisProperties.Pool pool = new RedisProperties.Pool();
            genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
            genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
            genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
            genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }

        return genericObjectPoolConfig;
    }

    /**
     * 创建Jedis连接池配置
     *
     * @return
     */
//    @Bean
    public static JedisPoolConfig getJedisPoolConfig(RedisProperties properties) {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        // 连接耗尽时是否阻塞, false报异常,ture阻塞直到超时, 默认true
        jedisPoolConfig.setBlockWhenExhausted(true);

        // 设置的逐出策略类名, 默认DefaultEvictionPolicy(当连接超过最大空闲时间,或连接数超过最大空闲连接数)
        jedisPoolConfig.setEvictionPolicyClassName("org.apache.commons.pool2.impl.DefaultEvictionPolicy");

        // 是否启用pool的jmx管理功能, 默认true
        jedisPoolConfig.setJmxEnabled(true);
        jedisPoolConfig.setJmxNamePrefix("pool");

        // 是否启用后进先出, 默认true
        jedisPoolConfig.setLifo(true);

        // Spring 1.x
//        jedisPoolConfig.setMaxTotal(properties.getPool().getMaxActive());// 最大连接数, 默认8个，控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
//        jedisPoolConfig.setMaxIdle(properties.getPool().getMaxIdle());// 最大空闲连接数, 默认8个，控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
//        jedisPoolConfig.setMaxWaitMillis(properties.getPool().getMaxWait()); //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1

        // Spring 2.x

        if (OptionalUtil.resolve(() -> properties.getJedis().getPool().getMaxActive()).isPresent()) {
            jedisPoolConfig.setMaxTotal(properties.getJedis().getPool().getMaxActive());// 最大连接数, 默认8个，控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
        }
        if (OptionalUtil.resolve(() -> properties.getJedis().getPool().getMaxIdle()).isPresent()) {
            jedisPoolConfig.setMaxIdle(properties.getJedis().getPool().getMaxIdle());// 最大空闲连接数, 默认8个，控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
        }
        if (OptionalUtil.resolve(() -> properties.getJedis().getPool().getMaxWait()).isPresent()) {
            jedisPoolConfig.setMaxWaitMillis(properties.getJedis().getPool().getMaxWait().toMillis()); //获取连接时的最大等待毫秒数(如果设置为阻塞时BlockWhenExhausted),如果超时就抛异常, 小于零:阻塞不确定的时间,  默认-1
        }

        // 逐出连接的最小空闲时间 默认1800000毫秒(30分钟)
        jedisPoolConfig.setMinEvictableIdleTimeMillis(1800000);

        // 最小空闲连接数, 默认0
//        jedisPoolConfig.setMinIdle(properties.getPool().getMinIdle());
        if (OptionalUtil.resolve(() -> properties.getJedis().getPool().getMinIdle()).isPresent()) {
            jedisPoolConfig.setMinIdle(properties.getJedis().getPool().getMinIdle());
        }

        // 每次逐出检查时 逐出的最大数目 如果为负数就是 : 1/abs(n), 默认3
        jedisPoolConfig.setNumTestsPerEvictionRun(3);

        // 对象空闲多久后逐出, 当空闲时间>该值 且 空闲连接>最大空闲数 时直接逐出,不再根据MinEvictableIdleTimeMillis判断  (默认逐出策略)
        jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(1800000);

        jedisPoolConfig.setTestOnBorrow(false); // 使用时进行扫描，确保都可用,在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；,在获取连接的时候检查有效性, 默认false
        jedisPoolConfig.setTestWhileIdle(false);// 在空闲时检查有效性, 默认false,Idle时进行连接扫描
        jedisPoolConfig.setTestOnReturn(true); // 还回线程池时进行扫描

        // 逐出扫描的时间间隔(毫秒) 如果为负数,则不运行逐出线程, 默认-1
        jedisPoolConfig.setTimeBetweenEvictionRunsMillis(-1);

        return jedisPoolConfig;
    }

    /**
     * 根据配置生成RedisTemplate
     *
     * @param redisProperties
     * @return
     */
    public static RedisTemplate getRedisTemplateFromJedis(RedisProperties redisProperties) {
        JedisConnectionFactory jedisConnectionFactory = getJedisConnectionFactory(redisProperties);
        return getRedisTemplate(jedisConnectionFactory);
    }

    public static StringRedisTemplate getStringRedisTemplateFromJedis(RedisProperties redisProperties) {
        JedisConnectionFactory jedisConnectionFactory = getJedisConnectionFactory(redisProperties);
        return getStringRedisTemplate(jedisConnectionFactory);
    }

    /**
     * 根据配置生成Lettuce的RedisTemplate
     *
     * @param redisProperties
     * @return
     */
    public static RedisTemplate getRedisTemplateFromLettuce(RedisProperties redisProperties) {
        LettuceConnectionFactory lettuceConnectionFactory = getLettuceConnectionFactory(redisProperties);
        return getRedisTemplate(lettuceConnectionFactory);
    }

    public static StringRedisTemplate getStringRedisTemplateFromLettuce(RedisProperties redisProperties) {
        LettuceConnectionFactory lettuceConnectionFactory = getLettuceConnectionFactory(redisProperties);
        return getStringRedisTemplate(lettuceConnectionFactory);
    }

    /**
     * 从配置文件中创建Redis模板
     *
     * @param redisProperties
     * @return
     */
    public static RedisTemplate getRedisTemplate(RedisProperties redisProperties) {
        if (redisProperties.getLettuce() != null) {
            return getRedisTemplateFromLettuce(redisProperties);
        }
        return getRedisTemplateFromJedis(redisProperties);
    }

    /**
     * 生成StringRedisTemplate
     *
     * @param redisProperties
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate(RedisProperties redisProperties) {
        if (redisProperties.getLettuce() != null) {
            return getStringRedisTemplateFromLettuce(redisProperties);
        }
        return getStringRedisTemplateFromJedis(redisProperties);
    }

    /**
     * 根据连接池生成RedisTemplate
     * 适配Jedis和Lettuce
     * <p>
     * ﻿Spring Data Redis提供了两个模板：
     * 1、RedisTemplate
     * 2、StringRedisTemplate，﻿如果key和value都是String类型，建议使用StringRedisTemplate
     * <p>
     * S﻿pring data redis提供多个序列化器
     * GenericToStringSerializer：使用Spring转换服务进行序列化；
     * JacksonJsonRedisSerializer：使用Jackson 1，将对象序列化为JSON；
     * Jackson2JsonRedisSerializer：使用Jackson 2，将对象序列化为JSON；
     * JdkSerializationRedisSerializer：使用Java序列化；
     * OxmSerializer：使用Spring O/X映射的编排器和解排器（marshaler和unmarshaler）实现序列化，用于XML序列化；
     * StringRedisSerializer：序列化String类型的key和value。
     * <p>
     * ﻿RedisTemplate会默认使用JdkSerializationRedisSerializer，这意味着key和value都会通过Java进行序列化。
     * StringRedisTemplate默认会使用StringRedisSerializer
     *
     * @param redisConnectionFactory
     * @return
     */
    public static RedisTemplate getRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 方式一：String格式的
        //        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
//        redisTemplate.setConnectionFactory(factory);

        // 方式二：可扩展的
        RedisTemplate redisTemplate = new RedisTemplate();

        // 方式三：StringTemplate
//        StringRedisTemplate redisTemplate = new StringRedisTemplate();

        redisTemplate.setConnectionFactory(redisConnectionFactory); // 配置连接池

        // 设置序列化方式
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = getJackson2JsonSerializer();
//        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = getFastJsonRedisSerializer();

        // 设置字符串的序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());// 配置Key的序列化方式，Long类型不可以出现，否则会出现异常信息;
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer); // 设置Value的序列化方式

        // 设置Hash的序列化方式
        //如果不配置Serializer，那么存储的时候缺省使用String，如果用User类型存储，那么会提示错误User can't cast to String！
//     redisTemplate.setHashKeySerializer(redisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 开启事务支持
//        redisTemplate.setEnableTransactionSupport(true); // 设置开启事务
        redisTemplate.afterPropertiesSet();//初始化配置内容

        return redisTemplate;
    }

    /**
     * 创建StringRedisTemplate
     *
     * @param redisConnectionFactory
     * @return
     */
    public static StringRedisTemplate getStringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 开启事务支持
//        redisTemplate.setEnableTransactionSupport(true); // 设置开启事务
        redisTemplate.afterPropertiesSet();//初始化配置内容

        return redisTemplate;
    }

    /**
     * 取出标准基础配置
     *
     * @param redisProperties
     * @return
     */
    public static RedisStandaloneConfiguration getStandaloneConfiguration(RedisProperties redisProperties) {
        // 普通连接
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());

        if (StringUtils.isNoneBlank(redisProperties.getPassword())) {
            redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        }
        return redisStandaloneConfiguration;
    }

    /**
     * 生成Lettuce的连接池
     *
     * @param redisProperties
     * @return
     */
    public static LettuceConnectionFactory getLettuceConnectionFactory(RedisProperties redisProperties) {
        LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(null != redisProperties.getTimeout() ? redisProperties.getTimeout().toMillis() : 10 * 1000))
                .poolConfig(getLettucePoolConfig(redisProperties))
                .build();
        LettuceConnectionFactory lettuceConnectionFactor = new LettuceConnectionFactory(getStandaloneConfiguration(redisProperties), clientConfig);
        lettuceConnectionFactor.afterPropertiesSet();
        return lettuceConnectionFactor;
    }

    /**
     * 从配置里面生成ConnectionFactory
     *
     * @param redisProperties
     * @return
     */
    public static JedisConnectionFactory getJedisConnectionFactory(RedisProperties redisProperties) {

        JedisConnectionFactory connectionFactory = null;

//        if (null != redisProperties.getSentinel()) { // 使用哨兵模式创建连接池
//            connectionFactory = new JedisConnectionFactory(RedisConnUtil.buildRedisSentinelConfiguration(redisProperties));
//        } else if (null != redisProperties.getCluster()) {
//            connectionFactory = new JedisConnectionFactory(RedisConnUtil.buildRedisClusterConfiguration(redisProperties));
//        } else {
//            connectionFactory = new JedisConnectionFactory();
//        }

        // Spring 1.x
//        // 配置基本的账号密码信息
//        connectionFactory.setDatabase(redisProperties.getDatabase());
//        connectionFactory.setHostName(redisProperties.getHost());
//        connectionFactory.setPassword(StringUtils.isBlank(redisProperties.getPassword()) ? null : redisProperties.getPassword());
//        connectionFactory.setPort(redisProperties.getPort());
////        connectionFactory.setTimeout(redisProperties.getTimeout());
//        connectionFactory.setTimeout(((int) (redisProperties.getTimeout().getSeconds())));
//        connectionFactory.setPoolConfig(jedisPoolConfig(redisProperties));

        // Spring 2.x

        // 普通连接
//        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
//        jedisClientConfiguration.connectTimeout(Duration.ofMillis(redisProperties.getTimeout().getSeconds()));// 连接超时设置

        // 这里需要注意的是，JedisConnectionFactoryJ对于Standalone模式的没有（RedisStandaloneConfiguration，JedisPoolConfig）的构造函数，对此
        // 我们用JedisClientConfiguration接口的builder方法实例化一个构造器，还得类型转换
        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisClientConfiguration = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        jedisClientConfiguration.poolConfig(getJedisPoolConfig(redisProperties));

        connectionFactory = new JedisConnectionFactory(getStandaloneConfiguration(redisProperties), jedisClientConfiguration.build());
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /**
     * 生成Jackson序列化
     * 需要引入Jackson的依赖 jackson-databind
     * 注：Jackson有Bug：
     * 1、
     *
     * @return
     */
    public static Jackson2JsonRedisSerializer getJackson2JsonSerializer() {

        // 使用JackSon做Redis序列化，Jackson2JsonRedisSerializer的序列化器
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(Object.class);

        // 解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        // 解决jackson2无法反序列化LocalDateTime的问题
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        om.registerModule(new JavaTimeModule());

        // 序列换成json时,将所有的long变成string,因为js中得数字类型不能包含所有的java long值
//        SimpleModule simpleModule = new SimpleModule();
//        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
//        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
//        om.registerModule(simpleModule);

        jackson2JsonRedisSerializer.setObjectMapper(om);

        return jackson2JsonRedisSerializer;
    }

    /**
     * 使用FastJson做为序列化工具
     *
     * @return
     */
    public static FastJsonRedisSerializer getFastJsonRedisSerializer() {
        FastJsonRedisSerializer<Object> fastJsonRedisSerializer = new FastJsonRedisSerializer<>(Object.class);//JSONObject
        // 全局开启AutoType，不建议使用
        // ParserConfig.getGlobalInstance().setAutoTypeSupport(true);

        // 建议使用这种方式，小范围指定白名单
        // ParserConfig.getGlobalInstance().addAccept("com.xiaolyuh.");

        return fastJsonRedisSerializer;
    }

    /**
     * 缓存Key生成规则
     *
     * @return
     */
    public static KeyGenerator getCacheKeyGenerater() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... objects) {
                // This will generate a unique key of the class name, the method name
                //and all method parameters appended.
                StringBuilder sb = new StringBuilder();
                sb.append(target.getClass().getName()).append(".");
                sb.append(method.getName()).append(".");
                for (Object obj : objects) {
                    sb.append(obj.toString());
                }
//                log.debug("keyGenerator=" + sb.toString());
                return sb.toString();
            }
        };
    }

    /**
     * 生成CacheManager缓存管理器
     *
     * @param redisConnectionFactory
     * @return
     */
    public static RedisCacheManager getCacheManager(RedisConnectionFactory redisConnectionFactory) {

        // Spring 1.x
        //        RedisCacheManager cacheManager = new RedisCacheManager(redisTemplate);
//        cacheManager.setDefaultExpiration(3600);// 默认数据过期时间，单位秒
//        cacheManager.setUsePrefix(true);
//        cacheManager.setCachePrefix(new RedisCachePrefix() {
//            private final RedisSerializer<String> serializer = new StringRedisSerializer();
//            private final String delimiter = ":";
//
//            public byte[] prefix(String cacheName) {
//                return this.serializer.serialize(cacheName.concat(this.delimiter));
//            }
//        });
//
//        return cacheManager;

        // Spring 2.x
//        return RedisCacheManager.create(redisConnectionFactory);

        // Spring 2.x 复杂版
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(getJackson2JsonSerializer()))
                .entryTtl(Duration.ofDays(7)) // 设置缓存有效期7天
                .disableCachingNullValues(); // 不缓存空值

        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(config)
                .build();
    }

}
