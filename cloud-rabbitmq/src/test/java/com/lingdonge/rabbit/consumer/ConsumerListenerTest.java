package com.lingdonge.rabbit.consumer;


import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * 消息队列接收类
 *
 * @RabbitListener用于注册Listener时使用的信息：如queue，exchange，key、ListenerContainerFactory和RabbitAdmin的bean name。
 */
@Slf4j
public class ConsumerListenerTest {


    /**
     * 最简单的直接消费
     *
     * @param content
     */
    @RabbitListener(queues = "test_queue")
//    @RabbitListener(queues = "test_queue", containerFactory = "pointTaskContainerFactory") // 指定连接池
    @RabbitHandler
    public void listenRabbitSimple(String content) {
        System.out.println("Receiver : " + content);
    }

    /**
     * FANOUT广播队列监听一.
     *
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception  这里异常需要处理
     */
    @RabbitListener(queues = {"FANOUT_QUEUE_A"})
    public void listenRabbitFanout(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.debug("FANOUT_QUEUE_A " + new String(message.getBody()));
    }

    /**
     * FANOUT广播队列监听二.
     *
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception   这里异常需要处理
     */
    @RabbitListener(queues = {"FANOUT_QUEUE_B"})
    public void listenRabbitFanoutB(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.debug("FANOUT_QUEUE_B " + new String(message.getBody()));
    }

    /**
     * DIRECT模式.
     *
     * @param message the message
     * @param channel the channel
     * @throws IOException the io exception  这里异常需要处理
     */
    @RabbitListener(queues = {"DIRECT_QUEUE"})
    public void listenRabbitDirect(Message message, Channel channel) throws IOException {
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        log.debug("DIRECT " + new String(message.getBody()));
    }

    /**
     * 绑定指定的交换机
     *
     * @param message
     */
    @RabbitListener(bindings = @QueueBinding(
            exchange = @Exchange("myElectronics"),
            key = "comput",
            value = @Queue("computElectronics")
    ))
    public void listenRabbitWithExchange(String message) {
        System.out.println(message);
    }

    @RabbitListener(containerFactory = "rabbitListenerContainerFactory", bindings = @QueueBinding(value = @Queue(value = "${mq.configuration.queue}", durable = "true"), exchange = @Exchange(value = "${mq.configuration.exchange}", type = ExchangeTypes.TOPIC), key = "${mq.configuration.key}"), admin = "rabbitAdmin")
    public void processTest(String message) {
        log.info("【队列消息】ReceiverMsg.milkTeaMQ ,milkTea={}", message);
    }


    @RabbitListener(bindings = @QueueBinding(
            key = "milkTea",
            value = @Queue("milkTeaOrder"),
            exchange = @Exchange("MyOrder")
    ))
    public void milkTeaMQ(String message) {
        log.info("【队列消息】ReceiverMsg.milkTeaMQ ,milkTea={}", message);
    }

    @RabbitListener(bindings = @QueueBinding(
            key = "mac",
            value = @Queue("macOrder"),
            exchange = @Exchange("MyOrder")
    ))
    public void macMQ(String message) {
        log.info("【队列消息】ReceiverMsg.macMQ ,macMQ={}", message);
    }


    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "${spring.rabbitmq.listener.order.queue.name}", durable = "${spring.rabbitmq.listener.order.queue.durable}"), exchange = @Exchange(value = "${spring.rabbitmq.listener.order.exchange.name}", durable = "${spring.rabbitmq.listener.order.exchange.durable}", type = "${spring.rabbitmq.listener.order.exchange.type}", ignoreDeclarationExceptions = "${spring.rabbitmq.listener.order.exchange.ignoreDeclarationeExceptions}"), key = "${spring.rabbitmq.listener.order.key}"))
    public void onOrderMessage(@Payload JSONObject object, Channel channel, @Headers Map<String, Object> headers)
            throws Exception {
        System.err.println("----------------------------------");
//        Order order = JsonConvertUtils.convertJSONToObject(object);
//        System.err.println("消费端Order: " + order.toString());
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
        channel.basicAck(deliveryTag, false);

    }

}
