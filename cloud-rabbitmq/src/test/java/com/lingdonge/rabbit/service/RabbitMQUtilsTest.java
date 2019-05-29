package com.lingdonge.rabbit.service;

import com.kyle.utility.threads.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RabbitMQUtilsTest {

    @Autowired
    private RabbitProperties rabbitMQProperties;

    @Bean
    public Queue queue() {
        return new Queue("queue:multithreading");
    }

    @RabbitListener(queues = "queue:multithreading")
    @RabbitHandler
    public void process(String hello) {
        System.out.println("Receiver  : " + hello);
    }

    /**
     * 并发测试
     */
    @Test
    public void sendMessage() {

        // 10线程测试 RabbitMQ 连接情况
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(100);

        for (int i = 1; i <= 100; i++) {
            final int index = i;
            fixedThreadPool.execute(new Runnable() {

                @Override
                public void run() {
                    try {
                        String threadName = Thread.currentThread().getName();
                        log.info("执行：" + index + "，线程名称：" + threadName);

                        RabbitMQUtils rabbitUtils = RabbitMQUtils.getInstance();
                        for (int p = 0; p < 100; p++) {

                            log.info("执行赋值：" + p + "，线程名称：" + threadName);

                            rabbitUtils.sendMsg("queue:multithreading", threadName + ":" + String.valueOf(p));
                        }

                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            });
        }

        //关闭线程池
        fixedThreadPool.shutdown();

        try {
            boolean loop = true;
            do { // 等待所有任务完成
                loop = !fixedThreadPool.awaitTermination(2, TimeUnit.SECONDS);
            } while (loop);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private RabbitMQUtils rabbitMQUtils;//rabbitmq操作类


    private static String test_queue_key = "test_queue";
    private static String test_fanout_key = "test_fanout";
    private static String test_topic_key = "test_topic";

    /**
     * 测试Fanout
     *
     * @throws IOException
     * @throws TimeoutException
     */
//    @Test
    public void testSendMsg() throws IOException, TimeoutException {


        rabbitMQUtils = new RabbitMQUtils(rabbitMQProperties);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Integer pos = 0;
                RabbitMQUtils rabbitMQUtils = null;
                try {
                    rabbitMQUtils = new RabbitMQUtils(rabbitMQProperties);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    pos++;
                    rabbitMQUtils.sendMsg(test_queue_key, pos + "");
                    System.out.println("Sending Message " + pos.toString());
                    ThreadUtil.sleep(3000);
                }
            }
        };

        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                RabbitMQUtils rabbitMQUtils = null;
                try {
                    rabbitMQUtils = new RabbitMQUtils(rabbitMQProperties);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (true) {
                    String message = rabbitMQUtils.recieveMsg(test_queue_key);

                    System.out.println("Fanout 1:" + message);

                    ThreadUtil.sleep(1000);
                }
            }
        };

        Thread thread1 = new Thread(runnable1);
        Thread thread = new Thread(runnable);

        thread1.start();
        thread.start();

        try {
            thread1.join();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Message Done");


    }

    /**
     * 测试Fanout发布
     *
     * @throws IOException
     * @throws TimeoutException
     */
    @Test
    public void testSendFanout() throws IOException, TimeoutException {

        rabbitMQUtils = new RabbitMQUtils(rabbitMQProperties);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Integer pos = 0;

                while (true) {
                    pos++;
                    rabbitMQUtils.sendFanoutMsg(test_fanout_key, String.valueOf(pos));
//                    rabbitMQUtils.sendFanoutMsg(test_fanout_key, String.valueOf(pos));
                    System.out.println("Sending Fanout Message " + String.valueOf(pos));
                    ThreadUtil.sleep(3000);
                }
            }
        };

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                Integer pos = 100000;

                while (true) {
                    pos++;
                    rabbitMQUtils.sendFanoutMsg(test_fanout_key, String.valueOf(pos));
//                    rabbitMQUtils.sendFanoutMsg(test_fanout_key, String.valueOf(pos));
                    System.out.println("Sending Fanout Message " + String.valueOf(pos));
                    ThreadUtil.sleep(3000);
                }
            }
        };

        Thread thread = new Thread(runnable);
        Thread thread2 = new Thread(runnable2);
        thread.start();
        thread2.start();

        try {
            thread.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Message Done");


    }

    /**
     * 测试Topic发布
     *
     * @throws IOException
     * @throws TimeoutException
     */
    @Test
    public void testSendTopic() throws IOException, TimeoutException {

        rabbitMQUtils = new RabbitMQUtils(rabbitMQProperties);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Integer pos = 0;

                while (true) {
                    pos++;
                    rabbitMQUtils.sendTopicMsg(test_topic_key, String.valueOf(pos));
//                    rabbitMQUtils.sendFanoutMsg(test_fanout_key, String.valueOf(pos));
                    System.out.println("Sending Fanout Message " + String.valueOf(pos));
                    ThreadUtil.sleep(3000);
                }
            }
        };

        Runnable runnable2 = new Runnable() {
            @Override
            public void run() {
                Integer pos = 100000;

                while (true) {
                    pos++;
                    rabbitMQUtils.recieveTopicMsg(test_topic_key, String.valueOf(pos));
//                    rabbitMQUtils.sendFanoutMsg(test_fanout_key, String.valueOf(pos));
                    System.out.println("Sending Fanout Message " + String.valueOf(pos));
                    ThreadUtil.sleep(3000);
                }
            }
        };

        Thread thread = new Thread(runnable);
        Thread thread2 = new Thread(runnable2);
        thread.start();
        thread2.start();

        try {
            thread.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Message Done");


    }


}