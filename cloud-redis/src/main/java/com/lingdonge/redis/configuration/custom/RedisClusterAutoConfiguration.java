package com.lingdonge.redis.configuration.custom;

import com.lingdonge.redis.util.RedisConnUtil;
import com.lingdonge.redis.service.RedisClusterUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisCluster;

/**
 * 集群环境配置
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
@Slf4j
public class RedisClusterAutoConfiguration {

    @Autowired
    private RedisProperties redisProperties;

    /**
     * Redis集群配置
     *
     * @return
     */
    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        return RedisConnUtil.buildRedisClusterConfiguration(redisProperties);
    }

    /**
     * 配置集群的连接池
     *
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory(redisClusterConfiguration(), RedisConnUtil.getJedisPoolConfig(redisProperties));
    }

    /**
     * Cluster集群配置
     *
     * @return
     */
    @Bean
    public JedisCluster jedisCluster() {
        return new JedisCluster(RedisConnUtil.getClusterNodes(redisProperties), RedisConnUtil.getJedisPoolGenericConfig(redisProperties));
    }

    /**
     * 构建RedisUtil的Bean实例
     *
     * @return
     * @throws Exception
     */
    @Bean
    public RedisClusterUtil redisClusterUtil() {
        log.info("<<<<<<<<<<<<<<< 加载 RedisClusterUtil 服务 >>>>>>>>>>>>>>>>>>");
        return new RedisClusterUtil(jedisCluster());
    }



}
