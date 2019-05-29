package com.lingdonge.db.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class HiKariDataSourceProperties {

    private String dataSourceClassName;

    private String url;

    private String username;

    private String password;

    private String poolName;

    private int connectionTimeout;

    private int maxLifetime;

    private int maximumPoolSize;

    private int minimumIdle;

    private int idleTimeout;

    private String defaultTenantId;

}
