package com.lingdonge.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;

@Slf4j
public class RedissonUtil {

    /**
     * 获取RedissonClient连接器
     *
     * @param redisProperties
     * @return
     */
    public static RedissonClient getRedissonClient(RedisProperties redisProperties) {

        Config config = new Config();

        long timeout = null != redisProperties.getTimeout() ? redisProperties.getTimeout().toMillis() : 10 * 1000;
        int poolSize = 10;

        if (null != redisProperties.getCluster()) {
            ClusterServersConfig serversConfig = config.useClusterServers()
                    .setScanInterval(2000) // 集群状态扫描间隔时间，单位是毫秒
                    .addNodeAddress(redisProperties.getCluster().getNodes().toArray(new String[0])) // 添加节点列表
                    .setMasterConnectionPoolSize(poolSize)
                    .setSlaveConnectionPoolSize(poolSize)
                    .setConnectTimeout((int) timeout);

            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                serversConfig.setPassword(redisProperties.getPassword());
            }
        } else if (null != redisProperties.getSentinel()) {
            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(redisProperties.getSentinel().getNodes().toArray(new String[0]))
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .setTimeout((int) timeout)
                    .setMasterConnectionPoolSize(poolSize)
                    .setSlaveConnectionPoolSize(poolSize);

            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
        } else {// 单机模式自动装配
            SingleServerConfig serverConfig = config.useSingleServer()
                    .setAddress(redisProperties.getHost())
                    .setTimeout(((int) (redisProperties.getTimeout().getSeconds())))
                    .setConnectionPoolSize(poolSize)
                    .setConnectionMinimumIdleSize(poolSize);

            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }

        }

        return Redisson.create(config);
    }

}
