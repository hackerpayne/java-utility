package com.lingdonge.rabbit.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 使用模板消息的测试代码
 */
@Slf4j
public class TemplateProducerTest {

    @Resource
    private AmqpTemplate amqpTemplate;

    /**
     * 直接发送消息
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(Object message) throws IOException {
        log.info("to send message:{}", message);
        amqpTemplate.convertAndSend("sendqueueKey", message);
    }

    /**
     * 直接发送，可以自定义消息的数据
     * @throws UnsupportedEncodingException
     */
    public void sendMessage() throws UnsupportedEncodingException {
        Message message = MessageBuilder.withBody("hello rabbit".getBytes("utf-8"))
                .setMessageId(System.currentTimeMillis() + "")
                .build();
        this.amqpTemplate.send("queueTestKey", message);

    }


}
