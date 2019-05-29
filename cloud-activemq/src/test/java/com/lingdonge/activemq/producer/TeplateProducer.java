package com.lingdonge.activemq.producer;

import com.lingdonge.activemq.queue.QueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.broker.region.Destination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TeplateProducer {


    @Autowired
    private JmsTemplate jmsMessagingTemplate;// 也可以注入JmsTemplate，JmsMessagingTemplate对JmsTemplate进行了封装


    /**
     * 发送消息
     *
     * @param destination 发送到的队列
     * @param message     待发送的消息
     */
    public void sendMessage(Destination destination, final String message) {
        jmsMessagingTemplate.convertAndSend(QueueConfig.MESSAGE_TEL, message);
    }

}
