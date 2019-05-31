package com.lingdonge.rabbit.producer;

import com.lingdonge.rabbit.SpringBaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;

/**
 * 使用模板消息的测试代码
 */
@EnableAutoConfiguration // 自动加载配置文件
@Slf4j
public class TemplateProducerTest extends SpringBaseTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private RabbitAdmin rabbitAdmin;


    /**
     * 初始化一个队列
     */
    public void initQueue(String queueName) {
        rabbitAdmin.declareQueue(new Queue(queueName, true));
    }

    @Test
    public void sendMessageSimple() {

        String queueName = "sendqueueKey";

        initQueue(queueName);

        for (int i = 0; i < 1000; i++) {
            rabbitTemplate.convertAndSend(queueName, "test:" + i);
            log.info("正在发送第【{}】条消息", i);
        }
        log.info("发送完成");
    }

    /**
     * 直接发送，可以自定义消息的数据
     */
    @Test
    public void sendMessage() throws UnsupportedEncodingException {


        Message message = MessageBuilder.withBody("hello rabbit".getBytes("utf-8"))
                .setMessageId(System.currentTimeMillis() + "")
                .build();
        this.rabbitTemplate.send("queueTestKey", message);

    }


}
