package com.lingdonge.db.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.Maps;
import com.lingdonge.db.configuration.properties.DruidProperties;
import com.lingdonge.db.dynamic.datasource.DruidDynamicDataSource;
import com.lingdonge.db.dynamic.datasource.service.MiddleTableService;
import com.lingdonge.db.util.DataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 动态数据源，不能单独玩了，得扫整个包才可以
 */
@Configuration
@EnableConfigurationProperties(DruidProperties.class) // 开启指定类的配置
@Slf4j
public class DynamicDataSourceAutoConfiguration {

    @Resource
    private DruidProperties properties;

    /**
     * Druid默认数据源配置
     *
     * @return
     */
    public DataSource defaultDataSource(DruidProperties properties) {
        log.info("<<<<<<<<<<<<<<< 设置 DruidDataSource 数据源，做为默认数据源 >>>>>>>>>>>>>>>>>>");
        DruidDataSource datasource = DataSourceBuilder.createDruidDataSource(properties);
        return datasource;
    }

    /**
     * 使用动态数据源，替换掉系统默认的数据源，否则无法管理所有的数据源
     * 初始化的时候加载默认的一个数据源就行了，后面的可以自动加载
     * 也可以初始化的时候就配置好所有的数据源，手动或者自动均可配置
     *
     * @return
     */
    @Bean
    @Primary
    public DataSource druidDynamicDataSource() {

        log.info("<<<<<<<<<<<<<<< 设置 druidDynamicDataSource 数据源，接替系统默认数据源做动态切换 >>>>>>>>>>>>>>>>>>");

        DruidDynamicDataSource druidDynamicDataSource = new DruidDynamicDataSource();

        Map<Object, Object> dataSources = Maps.newHashMap();
        dataSources.put(DataSourceBuilder.DEFAULT_DATASOURCE_KEY, defaultDataSource(properties));//添加默认数据源，也可以继续加更多的多个数据源，但是默认的这个是必备的

        druidDynamicDataSource.setTargetDataSources(dataSources);
        druidDynamicDataSource.afterPropertiesSet();//完成初始化操作

        return druidDynamicDataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(druidDynamicDataSource());
    }

    /**
     * 初始化数据源管理的服务，方便直接使用
     *
     * @return
     */
    @Bean
    public MiddleTableService middleTableService() {
        return new MiddleTableService();
    }

}
