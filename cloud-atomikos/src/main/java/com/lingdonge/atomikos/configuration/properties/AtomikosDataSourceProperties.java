package com.lingdonge.atomikos.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "atomikos.datasource")
@Data
public class AtomikosDataSourceProperties {

    /**
     *
     */
    private Integer maxPoolSize = 100;

    /**
     *
     */
    private Integer minPoolSize = 10;

    /**
     *
     */
    private int BorrowConnectionTimeout = 10 * 1000;

}
