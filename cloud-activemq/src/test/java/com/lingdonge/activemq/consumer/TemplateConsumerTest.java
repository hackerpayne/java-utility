package com.lingdonge.activemq.consumer;

import com.lingdonge.activemq.queue.QueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TemplateConsumerTest {


    /**
     * 发送短信
     *
     * @param messageTel
     */
    @JmsListener(destination = QueueConfig.MESSAGE_TEL, containerFactory = "queueListenerFactory")
    public void receiveTel1(String messageTel) {
        log.info("-----receive tel message-----");

    }

    /**
     * 消息二次返回队列继续处理
     *
     * @param text
     * @return
     */
    @JmsListener(destination = "mytest.queue")
    @SendTo("out.queue") // 处理完的消息，再进行一次返回
    public String receiveQueue(String text) {
        System.out.println("Consumer2收到的报文为:" + text);
        return "return message" + text;
    }
}