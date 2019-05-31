package com.lingdonge.atomikos.configuration;

import com.atomikos.jdbc.AbstractDataSourceBean;
import com.vladmihalcea.flexypool.FlexyPoolDataSource;
import com.vladmihalcea.flexypool.adaptor.AtomikosPoolAdapter;
import com.vladmihalcea.flexypool.config.Configuration;
import com.vladmihalcea.flexypool.connection.JdkConnectionProxyFactory;
import com.vladmihalcea.flexypool.strategy.IncrementPoolOnTimeoutConnectionAcquiringStrategy;
import com.vladmihalcea.flexypool.strategy.RetryConnectionAcquiringStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 数据源连接池协调器
 */
@org.springframework.context.annotation.Configuration
@Slf4j
public class AtomikosFlexyPoolConfiguration {

    public AtomikosFlexyPoolConfiguration() {
        log.info("加载[{}]类", this.getClass().getSimpleName());
    }

    @Resource
    private AbstractDataSourceBean poolingDataSource;

    @Value("${flexy.pool.uniqueId}")
    private String uniqueId;

    @Bean
    public Configuration<AbstractDataSourceBean> configuration() {
        return new Configuration.Builder<AbstractDataSourceBean>(
                uniqueId,
                poolingDataSource,
                AtomikosPoolAdapter.FACTORY
        ).setConnectionProxyFactory(JdkConnectionProxyFactory.INSTANCE)
                .setJmxEnabled(true) // 是否开启JMX监控
                .setMetricLogReporterMillis(TimeUnit.SECONDS.toMillis(5))
                .build();
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public FlexyPoolDataSource dataSource() {
        Configuration<AbstractDataSourceBean> configuration = configuration();
        return new FlexyPoolDataSource<AbstractDataSourceBean>(configuration,
                new IncrementPoolOnTimeoutConnectionAcquiringStrategy.Factory(5),
                new RetryConnectionAcquiringStrategy.Factory(2)
        );
    }


}
