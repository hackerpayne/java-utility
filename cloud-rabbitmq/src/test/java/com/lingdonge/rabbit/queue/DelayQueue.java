package com.lingdonge.rabbit.queue;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ延时队列配置
 */
public class DelayQueue {


    private static final String DELAY_QUEUE_PER_MESSAGE_TTL_NAME = "";
    private static final String DELAY_EXCHANGE_NAME = "";
    private static final String DELAY_PROCESS_QUEUE_NAME = "";
    private static final String DELAY_QUEUE_PER_QUEUE_TTL_NAME = "";
    private static final String QUEUE_EXPIRATION = "";



    /**
     * 实际消费队列
     *
     * @return
     */
    @Bean
    Queue delayProcessQueue() {
        return QueueBuilder.durable(DELAY_PROCESS_QUEUE_NAME)
                .build();
    }

    @Bean
    DirectExchange delayExchange() {
        return new DirectExchange(DELAY_EXCHANGE_NAME);
    }

    /**
     * 绑定到
     *
     * @param delayProcessQueue
     * @param delayExchange
     * @return
     */
    @Bean
    Binding dlxBinding(Queue delayProcessQueue, DirectExchange delayExchange) {
        return BindingBuilder.bind(delayProcessQueue)
                .to(delayExchange)
                .with(DELAY_PROCESS_QUEUE_NAME);
    }

}
