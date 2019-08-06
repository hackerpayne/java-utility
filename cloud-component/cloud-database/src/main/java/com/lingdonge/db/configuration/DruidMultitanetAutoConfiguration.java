package com.lingdonge.db.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.lingdonge.db.configuration.properties.DruidProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Druid多租户配置
 * Druid数据源：配置之后通过 http://127.0.0.1:8091/druid/ 进行访问
 * 默认账号密码admin/admin123
 */
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class) // 开启指定类的配置
@Slf4j
public class DruidMultitanetAutoConfiguration  {

    public DruidMultitanetAutoConfiguration(){
        log.info("加载[{}]类",this.getClass().getSimpleName());
    }

    @Resource
    private DruidProperties properties;
//
//    @Bean(name = "druidDataSource")     //声明其为Bean实例
//    @Primary  //在同样的DataSource中，首先使用被标注的DataSource
//    public DataSource dataSource() {
//
//        log.info("<<<<<<<<<<<<<<< 重写 DruidMultiTanentDataSource多租户 处理机制 >>>>>>>>>>>>>>>>>>");
//
//        DynamicDataSource multiTanentDataSource = new DynamicDataSource(properties);
//
//        DataSource dataSource = DataSourceBuilder.createDruidDataSource(properties);
//
//        // 方案一：使用动态数据源，需要配合 determineCurrentLookupKey 和 determineTargetDataSource 进行切换
//        multiTanentDataSource.setDefaultTargetDataSource(dataSource);//添加默认数据源
//
//        // 方案二：固定数据源，可以在入口这里就构造好。直接使用 determineCurrentLookupKey 进行切换
////        Map<Object, Object> targetDataSources = new HashMap<>();
////        targetDataSources.put("TenantOne", tenantOne());
////        targetDataSources.put("TenantTwo", tenantTwo());
////        multiTanentDataSource.setTargetDataSources(targetDataSources);
////        multiTanentDataSource.afterPropertiesSet();
//
//        return multiTanentDataSource;
//    }


}
