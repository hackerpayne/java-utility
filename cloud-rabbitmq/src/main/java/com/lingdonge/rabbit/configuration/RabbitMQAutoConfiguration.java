package com.lingdonge.rabbit.configuration;

import com.lingdonge.rabbit.service.RabbitMQUtils;
import com.lingdonge.rabbit.service.RabbitTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
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
@Configuration
@EnableRabbit
@Slf4j
public class RabbitMQAutoConfiguration {

    @Resource
    private RabbitProperties properties;

    @Resource
    private ConnectionFactory connectionFactory;

    /**
     * 默认是jdk的序列化转换器，我们自定义为json的
     *
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建管理员
     *
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin() {
        log.info("<<<<<<<<<<<<<<< 加载 RabbitAdmin 服务 >>>>>>>>>>>>>>>>>>");
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
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

        RabbitTemplate template = new RabbitTemplate(connectionFactory);

        // 使用jackson 消息转换器
        template.setMessageConverter(messageConverter());
        template.setEncoding("UTF-8");

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

    @Bean
    public RabbitTemplateUtil rabbitTemplateUtil() {
        log.info("<<<<<<<<<<<<<<< 加载 RabbitTemplateUtil 服务 >>>>>>>>>>>>>>>>>>");
        return new RabbitTemplateUtil(rabbitAdmin());
    }
}