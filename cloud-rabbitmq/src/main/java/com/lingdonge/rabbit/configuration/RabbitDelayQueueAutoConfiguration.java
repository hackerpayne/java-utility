package com.lingdonge.rabbit.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 延迟队列自动配置
 * 场景：适合固定时间之后消费的情况，比如订单30分钟之后自动删除这种情况。
 * 1、延时任务，丢进延时队列
 * 2、到达时间之后，进死信队列。
 * 3、死信队列，使用comsumeQueue来消费结果的数据
 */
//@Component
public class RabbitDelayQueueAutoConfiguration {

    /**
     * 延时消息队列。需要处理的消息投递到这里
     */
    private static String delayQueueName = "delayQueue";

    /**
     * 最后的消费队列。延迟之后的处理队列
     */
    private static String consumeQueueName = "consumerQueue";

    private static long delayTimeOut = 1000;// 死信时间

    /**
     * 配置延时的队列名称
     *
     * @return
     */
    @Bean
    public Queue delayQueue() {
        return QueueBuilder.durable(delayQueueName) //队列名称
                .withArgument("x-message-ttl", delayTimeOut) // 死信时间，过期之后，自动进入交换机
                .withArgument("x-dead-letter-exchange", getDelayExchangeName()) // 死信重新投递的交换机，消息在死信之后自动publish到交换机
                .withArgument("x-dead-letter-routing-key", consumeQueueName)//路由到队列的routingKey
                .build();
    }


    /**
     * 持久化队列，最后的消费者
     */
    @Bean
    public Queue delayConsumeQueue() {
        return new Queue(consumeQueueName, true);
    }

    /**
     * 自动注入为SimpleRabbitListenerContainerFactory的消息序列化转换器
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    private String getDelayExchangeName(){
        return "delay-exchange-" + delayQueueName + "-" + consumeQueueName;
    }

    /**
     * 持久化交换机，使用exchange-延时队列-消费队列的形式保存数据
     */
    @Bean(name = "delayQueueExchange")
    public FanoutExchange delayQueueExchange() {
        return new FanoutExchange(getDelayExchangeName(), true, false);
    }

    /**
     * 将队列和exchange绑定
     *
     * @return binding
     */
    @Bean
    public Binding bindingSmsExchangeSmsQueue() {
        return BindingBuilder.bind(delayConsumeQueue()).to(delayQueueExchange());
    }
}
