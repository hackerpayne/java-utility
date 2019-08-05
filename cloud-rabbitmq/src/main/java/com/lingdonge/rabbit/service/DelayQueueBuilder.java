package com.lingdonge.rabbit.service;


import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建延时队列插件的配置，使用时：
 * 注：本延时队列使用系统插件实现
 * getQueue() 建Bean
 * getExchange() 建Bean
 * getBinding() 建Bean
 */
@Getter
@Setter
@Slf4j
public class DelayQueueBuilder {

    private String queueName = "";
    private String exchangeName = "";
    private String routingName = "";

    public DelayQueueBuilder(String queueName) {
        this.queueName = queueName + ".queue";
        this.exchangeName = queueName + ".exchange";
        this.routingName = queueName + ".routingkey";
    }

    public Queue getQueue() {
        return new Queue(this.queueName);
    }

    /**
     * 自定义的Exchange，也可以是指定类型的Exchange
     *
     * @return
     */
    public CustomExchange getExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(this.exchangeName, "x-delayed-message", true, false, args);
    }

    public Binding getBinding() {
        return BindingBuilder.bind(getQueue()).to(getExchange()).with(this.routingName).noargs();
    }

    /**
     * 发送消息时使用如下的逻辑
     *
     * @param msg       消息内容
     * @param delayTime 延时时间
     */
    private void sendDelayMsg(String msg, Integer delayTime) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.convertAndSend(this.exchangeName, this.routingName, msg, a -> {
            a.getMessageProperties().setDelay(delayTime);
            return a;
        });
    }

    @RabbitListener(queues = "")
    private void receiveD(Message message, Channel channel) throws IOException {
        String msg = new String(message.getBody());
        log.info("当前时间：{},延时队列收到消息：{}", new Date().toString(), msg);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }


}
