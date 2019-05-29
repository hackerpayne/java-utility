package com.lingdonge.db.configuration.properties;//package com.kyle.spiderservice.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MySQL配置基本字段
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class MySQLProperties {

    private String url;

    private String username;

    private String password;

    private String driverClassName;

    private int maxActive;

    private int maxIdle;

    private int minIdle;


}
