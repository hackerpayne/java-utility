package com.lingdonge.db.configuration;

import com.lingdonge.db.configuration.properties.HiKariDataSourceProperties;
import com.lingdonge.db.util.DataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 配置HiKari数据连接池进行连接
 */
@Configuration
@EnableConfigurationProperties(HiKariDataSourceProperties.class) // 开启指定类的配置
@Slf4j
public class HiKariDataSourceAutoConfiguration {

    public HiKariDataSourceAutoConfiguration() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

    @Autowired
    private HiKariDataSourceProperties dataSourceProperties;

    /**
     * 配置Hikari主数据源
     *
     * @return
     */
    @Bean
    @Primary
    public DataSource primaryDataSource() {
        return DataSourceBuilder.createHikariDataSource(dataSourceProperties);
    }

}
