package com.lingdonge.rabbit.producer;

import com.lingdonge.rabbit.SpringBaseTest;
import com.lingdonge.rabbit.TestUtil;
import com.lingdonge.rabbit.service.RabbitMQUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

/**
 * 原生生产者配置
 */
@Slf4j
public class ProducerTest extends SpringBaseTest {


    //队列是否需要持久化
    public static final boolean DURABLE = true;

    public ProducerTest() {

    }

    /**
     * 正常发送消息测试
     */
    public static void normalProducer() {
        Connection connection = null;
        Channel channel = null;

        String QUEUE_NAME = "test_queue";  // 队列名称

        try {
            // 1、创建connection & channel
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            // 2、定义Queue
            channel.queueDeclare(QUEUE_NAME, DURABLE, false, false, null);

            // 3、发布消息
            String message;
            for (int i = 0; i < 100000; i++) {
                message = "消息：" + i;
                channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());//PERSISTENT_TEXT_PLAIN设置为持久化消息
            }
            System.out.println("消息发布完成！");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }

    }


    /**
     * 直接发送消息测试
     */
    public static void directProducer() {

        Connection connection = null;
        Channel channel = null;

        String EXCHANGE_NAME = "exchange";

        try {
            // 1.创建连接和通道
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            // 2.为通道声明direct类型的exchange
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            // 3.发送消息到指定的exchange,队列指定为空,由exchange根据情况判断需要发送到哪些队列
            String routingKey = "debug";
            String msg = " hello rabbitmq, I am " + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());

            System.out.println("product send a msg: " + msg);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }
    }


    public static void testTopicExchange() {
        Connection connection = null;
        Channel channel = null;

        String EXCHANGE_NAME = "exchange";

        try {

            // 1.创建连接和通道
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            // 2.为通道声明topic类型的exchange
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);

            // 3.发送消息到指定的exchange,队列指定为空,由exchange根据情况判断需要发送到哪些队列
            String routingKey = "info";
//            String routingKey = "log4j.error";
//            String routingKey = "logback.error";
//            String routingKey = "log4j.warn";
            String msg = " hello rabbitmq, I am " + routingKey;
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, msg.getBytes());
            System.out.println("product send a msg: " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }


    }

    /**
     * 使用Exchange进行数据分发
     */
    public static void exchangeTest() {

        String EXCHANGE_NAME = "topicExchange";

        Connection connection = null;
        Channel channel = null;
        try {
            // 1.创建连接和通道
            connection = TestUtil.getRabbitConn();
            channel = connection.createChannel();

            // 2.为通道声明exchange和exchange的类型
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            String msg = " hello rabbitmq, this is publish/subscribe mode";
            // 3.发送消息到指定的exchange,队列指定为空,由exchange根据情况判断需要发送到哪些队列
            channel.basicPublish(EXCHANGE_NAME, "", null, msg.getBytes());
            System.out.println("product send a msg: " + msg);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }

    }

    public static void testCommit() throws IOException {

        Connection connection = null;
        Channel channel = null;
        try {

            connection = TestUtil.getRabbitConn();
            String _queueName = "";

            // 创建信道
            channel = connection.createChannel();

            // 声明队列
            channel.queueDeclare(_queueName, true, false, false, null);
            String message = String.format("时间 => %s", new Date().getTime());

            channel.txSelect(); // 声明事务

            // 发送消息
            channel.basicPublish("", _queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            channel.txCommit(); // 提交事务

        } catch (Exception e) {
            channel.txRollback();
        } finally {
            RabbitMQUtils.closeChannel(channel);
            RabbitMQUtils.closeConnection(connection);
        }
    }


}
