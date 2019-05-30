package com.lingdonge.rabbit.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;

import javax.annotation.Resource;

/**
 * RabbitTemplate功能扩展
 *
 * RabbitAdmin 类 的底层实现就是从 Spring 容器中获取 exchange、Bingding、routingkey 以及queue 的 @bean 声明，然后使用 rabbitTemplate 的 execute 方法进行执行对应的声明、修改、删除等一系列 rabbitMQ 基础功能操作。例如添加交换机、删除一个绑定、清空一个队列里的消息等等
 *
 */
@Slf4j
public class RabbitTemplateUtil {

    @Resource
    private RabbitAdmin rabbitAdmin;

    public RabbitTemplateUtil() {

    }

    public RabbitTemplateUtil(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    /**
     * 获取未消息的消息的数量
     *
     * @param queueName
     * @return
     */
    public Integer getMessageCount(String queueName) {
        return (Integer) rabbitAdmin.getQueueProperties(queueName).get("QUEUE_MESSAGE_COUNT");
    }

    /**
     * 清空队列数据
     *
     * @param queueName
     */
    public void clearQueue(String queueName) {
        rabbitAdmin.purgeQueue(queueName, true); // 清空队列
    }
}
