package com.lingdonge.db.configuration;

import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * MyBatic自动加载配置
 * 使用MyBatis-Plus
 * ﻿mybatis-plus分页插件;文档:http://mp.baomidou.com
 */
@Configuration
//@EnableConfigurationProperties(MybatisPlusProperties.class)
@AutoConfigureAfter(DataSource.class)
@Slf4j
public class MyBatisPlusAutoConfiguration {

//    @Resource
//    private MybatisPlusProperties mybatisPlusProperties;
//
//    @Resource
//    private DataSource dataSource;

//    /**
//     * 异常：Invalid bound statement (not found) 解决方案
//     * https://mybatis.plus/guide/faq.html
//     * 如果不修改会仍然使用mybatis的SqlSessionFactoryBean而不是mytais-plus的，会出现此异常
//     *
//     * @return
//     * @throws Exception
//     */
//    @Bean
//    public SqlSessionFactory sqlSessionFactory() throws Exception {
//        MybatisSqlSessionFactoryBean sqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        sqlSessionFactoryBean.setMapperLocations(mybatisPlusProperties.resolveMapperLocations());
//        sqlSessionFactoryBean.setPlugins(new Interceptor[]{
//                paginationInterceptor(),
//                performanceInterceptor(),
//                optimisticLockerInterceptor()
//        });
//
//        sqlSessionFactoryBean.setConfigurationProperties(mybatisPlusProperties.getConfigurationProperties());
//        return sqlSessionFactoryBean.getObject();
//    }

    /**
     * sql性能分析插件，输出sql语句及所需时间
     *
     * @return
     */
    @Profile({"local", "localhost", "test", "dev"})// 指定环境打印SQL日志
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        // SQL是否格式化 默认false
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }

    /**
     * 分页插件，自动识别数据库类型
     *
     * @return
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    /**
     * 乐观锁插件
     *
     * @return
     */
    public OptimisticLockerInterceptor optimisticLockerInterceptor() {
        return new OptimisticLockerInterceptor();
    }


}
