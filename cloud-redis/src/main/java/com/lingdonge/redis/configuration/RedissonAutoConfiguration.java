package com.lingdonge.redis.configuration;

import com.lingdonge.redis.configuration.properties.RedissonProperties;
import com.lingdonge.redis.distributelock.RedissonDistributeLock;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson自动装配
 */
@Configuration
@ConditionalOnClass(Config.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    /**
     * 哨兵模式自动装配
     *
     * @return
     */
    @Bean
    public RedissonClient redissonSentinel() {
        Config config = new Config();

        if (StringUtils.isNotEmpty(redissonProperties.getMasterName())) { // 集群模式自动装配
            SentinelServersConfig serverConfig = config.useSentinelServers().addSentinelAddress(redissonProperties.getSentinelAddresses())
                    .setMasterName(redissonProperties.getMasterName())
                    .setTimeout(redissonProperties.getTimeout())
                    .setMasterConnectionPoolSize(redissonProperties.getMasterConnectionPoolSize())
                    .setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize());

            if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
                serverConfig.setPassword(redissonProperties.getPassword());
            }
        } else { // 单机模式自动装配
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress(redissonProperties.getAddress())
                    .setTimeout(redissonProperties.getTimeout())
                    .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                    .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());

            if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
                serverConfig.setPassword(redissonProperties.getPassword());
            }
        }

        return Redisson.create(config);
    }

    /**
     * 装配locker类，并将实例注入到RedissLockUtil中
     *
     * @return
     */
    @Bean
    public RedissonDistributeLock distributedLocker() {
        return new RedissonDistributeLock(redissonProperties);
    }

}
