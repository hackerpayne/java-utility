package com.lingdonge.db.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.lingdonge.db.configuration.properties.DruidProperties;
import com.lingdonge.db.util.DataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Druid 主从数据源
 */
@Configuration
@ConditionalOnClass(DruidDataSource.class)
@EnableConfigurationProperties(DruidProperties.class) // 开启指定类的配置
@Slf4j
public class DruidReadWriteAutoConfiguration {

    public DruidReadWriteAutoConfiguration() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

    @Resource
    private Environment env;

    @Bean(name = "readDataSource")
    public List<DataSource> readDataSource() {
        List<DataSource> list = new ArrayList<>();

        DruidProperties properties = DataSourceBuilder.buildDruidProperties(env, "spring.datasource.druid.read.");

        DruidDataSource dataSource = DataSourceBuilder.createDruidDataSource(properties);
        list.add(dataSource);

        //不使用的时候 在配置文件中 直接删除掉 也不会报错
//        for(int i =0 ; i< properties.getUrl().length; i++){
//            DruidDataSource dataSource = readDataSource.cloneDruidDataSource();
//            dataSource.setUrl(readProperties.getUrl()[i]);
//            dataSource.setUsername(readProperties.getUsername()[i]);
//            dataSource.setPassword(readProperties.getPassword()[i]);
//            list.add(dataSource);
//        }
        return list;
    }

}
