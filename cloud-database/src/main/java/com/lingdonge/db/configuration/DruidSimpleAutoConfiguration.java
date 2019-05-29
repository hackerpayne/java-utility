package com.lingdonge.db.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.lingdonge.db.configuration.properties.DruidProperties;
import com.lingdonge.db.util.DataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * Druid数据源：单个数据源
 * 配置之后通过 http://127.0.0.1:8091/druid/ 进行访问
 * 默认账号密码admin/admin123
 */
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class) // 开启指定类的配置
//@ConditionalOnProperty("druid.enabled")
//@ConditionalOnBean(DruidMakerConfiguration.Marker.class) //只有开启注解才能使用
@Slf4j
public class DruidSimpleAutoConfiguration {

    public DruidSimpleAutoConfiguration() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

    @Resource
    private DruidProperties properties;

    /**
     * Druid默认数据源配置
     *
     * @return
     */
    @Bean(name = "druidDataSource")     //声明其为Bean实例
    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
    public DataSource dataSource() {
        log.info("<<<<<<<<<<<<<<< 设置 DruidDataSource 数据源 >>>>>>>>>>>>>>>>>>");
        DruidDataSource datasource = DataSourceBuilder.createDruidDataSource(properties);
        return datasource;
    }


}
