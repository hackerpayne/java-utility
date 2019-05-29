package com.lingdonge.rabbit.queue;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * name:queue的名字。
 * durable:是否为持久的。默认是true，RabbitMQ重启后queue依然存在。
 * auto-delete:表示消息队列没有在使用时将被自动删除。默认是false。
 * exclusive:表示该消息队列是否只在当前connection生效。默认false。
 */
@Configuration
public class TestQueue {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 定义普通的一个队列
     *
     * @return
     */
    @Bean
    public Queue Queue() {
        return new Queue("hello", true);
    }

    //发送者
    public void send() {
        String context = "hi, i am message 1";
        System.out.println("Sender : " + context);
        this.rabbitTemplate.convertAndSend("object", context);
    }

    /**
     * 接受者
     *
     * @param user
     */
    @RabbitHandler
    public void process(String user) {
        System.out.println("Receiver object : " + user);
    }

}
