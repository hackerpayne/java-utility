package com.lingdonge.rabbit.service;


import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;

/**
 * 创建延时队列，自带的实现不能动态让时间延迟，
 * 场景：适合固定时间之后消费的情况，比如订单30分钟之后自动删除这种情况。
 * 1、延时任务，丢进延时队列
 * 2、到达时间之后，进死信队列。
 * 3、死信队列，使用comsumeQueue来消费结果的数据
 */
@Getter
@Setter
@Slf4j
public class DelayQueueSimpleBuilder {

    private String queueName = "";
    private String exchangeName = "";
    private String routingName = "";
    private Long delayTimeOut = 0L;

    /**
     * 不指定死信时间，需要发送时指定
     *
     * @param queueName
     */
    public DelayQueueSimpleBuilder(String queueName) {
        this(queueName, 0L);
    }

    public DelayQueueSimpleBuilder(String queueName, Long delayTimeOut) {
        this.queueName = queueName + ".queue";
        this.exchangeName = queueName + ".exchange";
        this.routingName = queueName + ".routingkey";
        this.delayTimeOut = delayTimeOut;
    }

    public Queue getQueue() {
        QueueBuilder queueBuilder = QueueBuilder.durable(queueName) //队列名称
                .withArgument("x-dead-letter-exchange", exchangeName) // 死信重新投递的交换机，消息在死信之后自动publish到交换机，DLX，dead letter发送到的exchange
                .withArgument("x-dead-letter-routing-key", this.routingName) // 路由到队列的routingKey，dead letter携带的routing key
                ;
        if (delayTimeOut > 0) {
            queueBuilder.withArgument("x-message-ttl", delayTimeOut); // 死信时间，过期之后，自动进入交换机

        }
        return queueBuilder.build();
    }

    public FanoutExchange getFanoutExchange() {
        return new FanoutExchange(this.exchangeName, true, false);
    }

    public Binding getBinding() {
        return BindingBuilder.bind(getQueue()).to(getFanoutExchange());
    }

}
