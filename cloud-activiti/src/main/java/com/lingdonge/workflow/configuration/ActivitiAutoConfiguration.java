package com.lingdonge.workflow.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.activiti.spring.SpringAsyncExecutor;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.AbstractProcessEngineAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
public class ActivitiAutoConfiguration extends AbstractProcessEngineAutoConfiguration {

    @Bean("activitiDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.activiti.datasource")
    public DataSourceProperties activitiDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("activitiDataSource")
    public HikariDataSource activitiDataSource() {
        DataSourceProperties activitiDataSourceProperties = activitiDataSourceProperties();
        return (HikariDataSource) activitiDataSourceProperties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean("insuranceActivitiConfig")
    public SpringProcessEngineConfiguration insuranceActivitiConfig(PlatformTransactionManager transactionManager,
                                                                    SpringAsyncExecutor springAsyncExecutor) throws IOException {
        SpringProcessEngineConfiguration springProcessEngineConfiguration = baseSpringProcessEngineConfiguration(
                activitiDataSource(),
                transactionManager,
                springAsyncExecutor);

        return springProcessEngineConfiguration;
    }

}
