package com.lingdonge.rabbit.service;

import com.lingdonge.core.threads.ThreadUtil;
import com.lingdonge.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
public class RabbitMQUtilsTemplateConsumerTest {
    private RabbitMQUtils rabbitMQUtils;//rabbitmq操作类

    @Autowired
    private RabbitProperties rabbitMQProperties;

    private static String test_queue_key = "test_queue";
    private static String test_fanout_key = "test_fanout";
    private static String test_topic_key = "test_topic";

    /**
     * 测试Fanout发布
     *
     * @throws IOException
     * @throws TimeoutException
     */
    @Test
    public void testRecieveMessage() throws IOException, TimeoutException {

        System.out.println("Test Recieve Message From RabbitMQ");
        rabbitMQUtils = new RabbitMQUtils(rabbitMQProperties);

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String message = rabbitMQUtils.recieveFanoutMsg(test_fanout_key);
                    System.out.println(StringUtils.format("Fanout Thread [{}] Get Message [{}]", Thread.currentThread().getId(), message));
                    ThreadUtil.sleep(1000);
                }
            }
        };

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String message = rabbitMQUtils.recieveFanoutMsg(test_fanout_key);
                    System.out.println(StringUtils.format("Fanout Thread [{}] Get Message [{}]", Thread.currentThread().getId(), message));
                    ThreadUtil.sleep(1000);
                }
            }
        };
        Runnable runnable3 = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String message = rabbitMQUtils.recieveFanoutMsg(test_fanout_key);
                    System.out.println(StringUtils.format("Fanout Thread [{}] Get Message [{}]", Thread.currentThread().getId(), message));
                    ThreadUtil.sleep(1000);
                }
            }
        };

        Thread thread1 = new Thread(runnable1);
        Thread thread2 = new Thread(runnable2);
        Thread thread3 = new Thread(runnable3);

        thread1.start();
        thread2.start();
        thread3.start();

        try {
            thread1.join();
            thread2.join();
            thread3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Message Done");

    }

}