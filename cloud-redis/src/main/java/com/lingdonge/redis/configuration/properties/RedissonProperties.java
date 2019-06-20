package com.lingdonge.redis.configuration.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 设置Redisson配置
 */
@ConfigurationProperties(prefix = "redisson")
@Getter
@Setter
public class RedissonProperties {

    private int timeout = 3000;

    private String address = "127.0.0.1";

    private String password;

    private int database = 0;

    private int connectionPoolSize = 64;

    private int connectionMinimumIdleSize = 10;

    private int slaveConnectionPoolSize = 250;

    private int masterConnectionPoolSize = 250;

    private String[] sentinelAddresses;

    private String masterName;

}