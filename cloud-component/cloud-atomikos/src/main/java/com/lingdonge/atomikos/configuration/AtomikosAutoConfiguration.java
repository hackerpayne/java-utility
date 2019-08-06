package com.lingdonge.atomikos.configuration;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.lingdonge.db.util.DataSourceBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jta.atomikos.AtomikosProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Properties;

/**
 * http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-jta.html
 */
@Configuration
@EnableTransactionManagement
//@EnableTransactionManagement(proxyTargetClass = true)
@Slf4j
public class AtomikosAutoConfiguration {

    public AtomikosAutoConfiguration() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

    @Resource
    private AtomikosProperties atomikosProperties;

    @Bean(name = "systemDataSource")
    @Primary
    @Autowired
    public DataSource systemDataSource(Environment env) {
        AtomikosDataSourceBean atomikosDataSourceBean = new AtomikosDataSourceBean();

        atomikosDataSourceBean.setXaDataSourceClassName(DataSourceBuilder.DRUID_XA_CLASS_NAME);//使用Druid
        atomikosDataSourceBean.setUniqueResourceName("systemDB");
        atomikosDataSourceBean.setPoolSize(atomikosProperties.getMaxActives());

        // 设置单独的配置
        Properties properties = DataSourceBuilder.buildDruidToProperties(env, "spring.datasource.druid.systemDB.");
        atomikosDataSourceBean.setXaProperties(properties);

        return atomikosDataSourceBean;

    }

    @Autowired
    @Bean(name = "businessDataSource")
    public AtomikosDataSourceBean businessDataSource(Environment env) {

        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setXaDataSourceClassName(DataSourceBuilder.DRUID_XA_CLASS_NAME);
        ds.setUniqueResourceName("businessDB");
        ds.setPoolSize(5);

        Properties prop = DataSourceBuilder.buildDruidToProperties(env, "spring.datasource.druid.businessDB.");
        ds.setXaProperties(prop);

        return ds;
    }

    @Bean("sysJdbcTemplate")
    public JdbcTemplate sysJdbcTemplate(@Qualifier("systemDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

    @Bean("busJdbcTemplate")
    public JdbcTemplate busJdbcTemplate(@Qualifier("businessDataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }

}
