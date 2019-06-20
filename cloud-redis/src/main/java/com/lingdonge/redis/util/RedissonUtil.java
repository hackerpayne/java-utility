package com.lingdonge.redis.util;

import com.lingdonge.redis.configuration.properties.RedissonProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;

@Slf4j
public class RedissonUtil {

    /**
     * 获取RedissonClient连接器
     *
     * @param redissonProperties
     * @return
     */
    public static RedissonClient getRedissonClient(RedissonProperties redissonProperties) {

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

}
