package com.lingdonge.rabbit.consumer;

import com.lingdonge.rabbit.TestUtil;
import com.lingdonge.rabbit.service.RabbitMQUtils;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 原生代码测试
 */
@Slf4j
public class ConsumerTest {

    public static void main(String[] args) {
        System.out.println("*** Work ***");

        normalConsumer();
    }

    /**
     * 普通手工消费者
     */
    public static void normalConsumer() {

        Connection connection = null;
        Channel channel = null;

        String QUEUE_NAME = "test_queue";  // 队列名称

        try {
            //1.connection & channel
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            //2.queue
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            //3. consumer instance
            Consumer consumer = new DefaultConsumer(channel) {
                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
                                           byte[] body) throws IOException {
                    String msg = new String(body, "UTF-8");
                    //deal task
                    doWork(msg);

                    // 注意，由于使用了basicConsume的autoAck特性，因此这里就不需要手动执行
//                    channel.basicAck(envelope.getDeliveryTag(), false);
                }
            };

            //4.do consumer
            boolean autoAck = true;
            channel.basicQos(1);//公平分发，每次处理1条数据
//            channel.basicQos(10, false); // Per consumer limit
//            channel.basicQos(15, true);  // Per channel limit

            channel.basicConsume(QUEUE_NAME, autoAck, consumer); // 指定消费队列和应答模式

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }
    }

    public static void directConsumer() {

        String EXCHANGE_NAME = "directExchange";

        Connection connection = null;
        Channel channel = null;
        try {
            // 1.创建连接和通道
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            // 2.为通道声明direct类型的exchange
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            // 3.创建随机名字的队列
            String queueName = channel.queueDeclare().getQueue();

            // 4.建立exchange和队列的绑定关系
            String[] bindingKeys = {"error", "info", "debug"};
//            String[] bindingKeys = { "error" };
            for (int i = 0; i < bindingKeys.length; i++) {
                channel.queueBind(queueName, EXCHANGE_NAME, bindingKeys[i]);
                System.out.println(" **** LogDirectReciver keep alive ,waiting for " + bindingKeys[i]);
            }

            // 5.通过回调生成消费者并进行监听
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {

                    // 获取消息内容然后处理
                    String msg = new String(body, "UTF-8");
                    System.out.println("*********** LogDirectReciver" + " get message :[" + msg + "]");
                }
            };
            // 6.消费消息
            channel.basicConsume(queueName, true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }
    }

    public static void topicConsumer() {

        String EXCHANGE_NAME = "topicExchange";

        Connection connection = null;
        Channel channel = null;
        try {
            // 1.创建连接和通道
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            // 2.为通道声明topic类型的exchange
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            // 3.创建随机名字的队列
            String queueName = channel.queueDeclare().getQueue();

            // 4.建立exchange和队列的绑定关系
            String[] bindingKeys = {"#"};
//            String[] bindingKeys = { "log4j.*", "#.error" };
//            String[] bindingKeys = { "*.error" };
//            String[] bindingKeys = { "log4j.warn" };
            for (int i = 0; i < bindingKeys.length; i++) {
                channel.queueBind(queueName, EXCHANGE_NAME, bindingKeys[i]);
                System.out.println(" **** LogTopicReciver keep alive ,waiting for " + bindingKeys[i]);
            }

            // 5.通过回调生成消费者并进行监听
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {

                    // 获取消息内容然后处理
                    String msg = new String(body, "UTF-8");
                    System.out.println("*********** LogTopicReciver" + " get message :[" + msg + "]");
                }
            };
            // 6.消费消息
            channel.basicConsume(queueName, true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消息Exchange里面的数据
     */
    public static void exchangeConsumer() {

        Connection connection = null;
        Channel channel = null;

        String EXCHANGE_NAME = "topicExchange";

        try {
            // 1.创建连接和通道
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            // 2.为通道声明exchange以及exchange类型
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            // 3.创建随机名字的队列
            String queueName = channel.queueDeclare().getQueue();

            // 4.建立exchange和队列的绑定关系
            channel.queueBind(queueName, EXCHANGE_NAME, "");
            System.out.println(" **** Consumer1 keep alive ,waiting for messages, and then deal them");
            // 5.通过回调生成消费者并进行监听
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) throws IOException {

                    // 获取消息内容然后处理
                    String msg = new String(body, "UTF-8");
                    System.out.println("*********** Consumer1" + " get message :[" + msg + "]");
                }
            };
            // 6.消费消息
            channel.basicConsume(queueName, true, consumer);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }

    }

    private static void doWork(String msg) {
        try {
            System.out.println("**** deal task begin :" + msg);

            //假装task比较耗时，通过sleep（）来模拟需要消耗的时间
            if ("sleep".equals(msg)) {
                Thread.sleep(1000 * 60);
            } else {
                Thread.sleep(1000);
            }

            System.out.println("**** deal task finish :" + msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
