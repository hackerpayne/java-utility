package com.lingdonge.db.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.fastsql.SQLFactory;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@AutoConfigureAfter(DataSource.class)
@Slf4j
public class FastSQLAutoConfiguration {

    @Resource
    private DataSource dataSource;

    /**
     * 创建FastSQL的连接工厂
     *
     * @return
     */
    @Bean
    public SQLFactory buildSqlFactory() {
        SQLFactory sqlFactory = new SQLFactory();
        sqlFactory.setDataSource(dataSource);
        return sqlFactory;
    }
}
