package com.lingdonge.activemq.configuration;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.springframework.boot.autoconfigure.jms.activemq.ActiveMQProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;

import javax.annotation.Resource;

/**
 * ActiveMQ配置文件
 */
@Configuration
public class ActiveMQAutoConfiguration {

    /**
     * 配置文件
     */
    @Resource
    private ActiveMQProperties properties;

    /**
     * 配置连接池
     *
     * @return
     */
    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(properties.getUser(), properties.getPassword(), properties.getBrokerUrl());

        activeMQConnectionFactory.setTrustAllPackages(true);// 信任所有包
        return activeMQConnectionFactory;
    }

    /**
     * 配置连接池
     *
     * @return
     */
    @Bean
    public PooledConnectionFactory pooledConnectionFactory() {
        PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory();
        pooledConnectionFactory.setMaxConnections(properties.getPool().getMaxConnections());
        return pooledConnectionFactory;
    }

    /**
     * 配置监听器
     *
     * @return
     */
    @Bean
    public JmsListenerContainerFactory<?> queueListenerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setPubSubDomain(false);
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("3-10");//设置并发量
        return factory;
    }


    @Bean
    public JmsMessagingTemplate jmsMessagingTemplate(ActiveMQConnectionFactory connectionFactory) {
        return new JmsMessagingTemplate(connectionFactory);
    }


}
