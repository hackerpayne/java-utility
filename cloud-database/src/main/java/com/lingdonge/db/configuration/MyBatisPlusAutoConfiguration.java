package com.lingdonge.db.configuration;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * MyBatic自动加载配置
 * 使用MyBatis-Plus
 * ﻿mybatis-plus分页插件;文档:http://mp.baomidou.com
 */
@Configuration
@EnableConfigurationProperties(MybatisPlusProperties.class)
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
    @Bean
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        // SQL是否格式化 默认false
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }

    /**
     * 开启逻辑删除功能，使用时：
     * <p>
     * 添加@TableLogic注解到
     * private Integer deleted;
     * 使用时：
     * 删除时 update user set deleted=1 where id =1 and deleted=0
     * 查找时 select * from user where deleted=0
     */
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
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

   /*
    * oracle数据库配置JdbcTypeForNull
    * 参考：https://gitee.com/baomidou/mybatisplus-boot-starter/issues/IHS8X
    不需要这样配置了，参考 yml:
    mybatis-plus:
      confuguration
        dbc-type-for-null: 'null'
   @Bean
   public ConfigurationCustomizer configurationCustomizer(){
       return new MybatisPlusCustomizers();
   }

   class MybatisPlusCustomizers implements ConfigurationCustomizer {

       @Override
       public void customize(org.apache.ibatis.session.Configuration configuration) {
           configuration.setJdbcTypeForNull(JdbcType.NULL);
       }
   }
   */


}
