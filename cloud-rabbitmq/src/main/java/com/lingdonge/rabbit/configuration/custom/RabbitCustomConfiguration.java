package com.lingdonge.rabbit.configuration.custom;

import com.lingdonge.rabbit.service.RabbitMQUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ自动配置加载
 * 四种Exchange类型：
 * Direct：完全根据key进行投递的，例如，绑定时设置了routing key为”abc”，那么客户端提交的消息，只有设置了key为”abc”的才会投递到队列。
 * Topic：对key进行模式匹配后进行投递，符号”#”匹配一个或多个词，符号”*”匹配正好一个词。例如”abc.#”匹配”abc.def.ghi”，”abc.*”只匹配”abc.def”。
 * Fanout：不需要key，它采取广播模式，一个消息进来时，投递到与该交换机绑定的所有队列。
 * Headers:我们可以不考虑它。
 */
//@Configuration
@EnableRabbit
@Slf4j
public class RabbitCustomConfiguration {

    @Resource
    private RabbitProperties properties;

    /**
     * 创建监听器工厂
     *
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());

        RabbitProperties.Listener listenerConfig = properties.getListener();
        factory.setAutoStartup(listenerConfig.getSimple().isAutoStartup());

        //设置确认模式手工确认,AcknowledgeMode.MANUAL,None或者Auto
        if (listenerConfig.getSimple().getAcknowledgeMode() != null) {
            factory.setAcknowledgeMode(listenerConfig.getSimple().getAcknowledgeMode());
        }

        // 并发消费者数量
        if (listenerConfig.getSimple().getConcurrency() != null) {
            factory.setConcurrentConsumers(listenerConfig.getSimple().getConcurrency());
        }

        // 最大并发消费者Consumer数量
        if (listenerConfig.getSimple().getMaxConcurrency() != null) {
            factory.setMaxConcurrentConsumers(listenerConfig.getSimple().getMaxConcurrency());
        }

        // 每个消费者获取最大投递数量 默认50
        if (listenerConfig.getSimple().getPrefetch() != null) {
            factory.setPrefetchCount(listenerConfig.getSimple().getPrefetch());
        }

        if (listenerConfig.getSimple().getTransactionSize() != null) {
            factory.setTxSize(listenerConfig.getSimple().getTransactionSize());
        }

        return factory;
    }

    /**
     * 创建连接工厂
     *
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(properties.getHost() + ":" + properties.getPort());
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getPassword());
        connectionFactory.setVirtualHost(properties.getVirtualHost());
        connectionFactory.setPublisherConfirms(properties.isPublisherConfirms()); //必须要设置
        connectionFactory.setChannelCacheSize(properties.getCache().getChannel().getSize());//缓存大小，最好必填，可以有效解决丢失问题

        return connectionFactory;
    }

    /**
     * 创建管理员
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        log.info("<<<<<<<<<<<<<<< 加载 RabbitAdmin 服务 >>>>>>>>>>>>>>>>>>");
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        return rabbitAdmin;
    }

    /**
     * 创建模板
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {

        log.info("<<<<<<<<<<<<<<< 加载 RabbitTemplate 服务 >>>>>>>>>>>>>>>>>>");

        RabbitTemplate template = new RabbitTemplate(connectionFactory());

//        // 使用jackson 消息转换器
//        template.setMessageConverter(new Jackson2JsonMessageConverter());
//        template.setEncoding("UTF-8");
////        开启returncallback     yml 需要 配置    publisher-returns: true
//        template.setMandatory(true);
//        template.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
//            String correlationId = message.getMessageProperties().getCorrelationIdString();
//            logger.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
//        });
//        //        消息确认  yml 需要配置   publisher-returns: true
//        template.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                logger.debug("消息发送到exchange成功,id: {}", correlationData.getId());
//            } else {
//                logger.debug("消息发送到exchange失败,原因: {}", cause);
//            }
//        });

        return template;
    }

    /**
     * 向Spring中注册RabbitMQ事务管理器
     * 使用时：设置通道为Transaction类型
     * rabbitTemplate.setChannelTransacted(true);
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory) {
        return new RabbitTransactionManager(connectionFactory);
    }

    /**
     * 加载原生的操作辅助类
     *
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    @Bean
    public RabbitMQUtils rabbitMQUtils() throws IOException, TimeoutException {
        log.info("<<<<<<<<<<<<<<< 加载 RabbitMQUtils 服务 >>>>>>>>>>>>>>>>>>");
        return new RabbitMQUtils(properties);
    }

}