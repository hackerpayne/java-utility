package com.lingdonge.activemq.service;

import com.lingdonge.activemq.configuration.properties.ActiveMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;

import javax.jms.*;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

/**
 * ActiveMQ常用方法
 */
@Slf4j
public class ActiveMQUtils {

    //ConnectionFactory ：连接工厂，JMS 用它创建连接
    private ConnectionFactory connectionFactory;

    // Connection ：JMS 客户端到JMS Provider 的连接
    private Connection connection = null;

    private Session sessionProducer;

    private Session sessionConsumer;

    private ActiveMQConfig config;

    /**
     * 构造函数
     *
     * @param config
     * @throws JMSException
     */
    public ActiveMQUtils(ActiveMQConfig config) throws JMSException {
        this.config = config;

        connectionFactory = new ActiveMQConnectionFactory(this.config.getUser(), this.config.getPassword(), this.config.getBrokerUrl());

        // 构造从工厂得到连接对象
        connection = connectionFactory.createConnection();

        // 启动
        connection.start();
    }

    /**
     * 往队列里面发送消息
     *
     * @param queueName
     * @param msg
     * @throws JMSException
     */
    public void sendMsg(String queueName, String msg) throws JMSException {

        try {


            //第一个参数是是否是事务型消息，设置为true,第二个参数无效
            //第二个参数是
            //Session.AUTO_ACKNOWLEDGE为自动确认，客户端发送和接收消息不需要做额外的工作。异常也会确认消息，应该是在执行之前确认的
            //Session.CLIENT_ACKNOWLEDGE为客户端确认。客户端接收到消息后，必须调用javax.jms.Message的acknowledge方法。jms服务器才会删除消息。可以在失败的
            //时候不确认消息,不确认的话不会移出队列，一直存在，下次启动继续接受。接收消息的连接不断开，其他的消费者也不会接受（正常情况下队列模式不存在其他消费者）
            //DUPS_OK_ACKNOWLEDGE允许副本的确认模式。一旦接收方应用程序的方法调用从处理消息处返回，会话对象就会确认消息的接收；而且允许重复确认。在需要考虑资源使用时，这种模式非常有效。
            // 获取操作连接
            sessionProducer = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);

            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            Destination destination = sessionProducer.createQueue(queueName);

            // 得到消息生成者【发送者】
            MessageProducer producer = sessionProducer.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);// 设置是否持久化

            TextMessage message = sessionProducer.createTextMessage(msg);

            // 发送消息到目的地方
            producer.send(message);

            sessionProducer.commit();//确认提交

//            producer.close();

//            session.close();
        } catch (Exception ex) {
            log.error("sendMsg发生异常", ex);
        } finally {
//            if (connection != null)
//                connection.close();

        }

    }

    /**
     * 从队列里面接收消息
     *
     * @param queueName
     */
    public String recieveMsg(String queueName) throws JMSException {

        String messageText = null;

        try {

            // 获取操作连接
            sessionConsumer = connection.createSession(Boolean.FALSE, Session.CLIENT_ACKNOWLEDGE);

            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            // 增加prefetchSize参数，一次收10条消息进行消费，参考：http://activemq.apache.org/destination-options.html
            Destination destination = sessionConsumer.createQueue(queueName + "?consumer.prefetchSize=10");

            MessageConsumer consumer = sessionConsumer.createConsumer(destination);//创建消费者

            // 设置接收者接收消息的时间，为了便于测试，这里谁定为100s
            // 接收消息，参数：接收消息的超时时间，为0的话则不超时，receive返回下一个消息，但是超时了或者消费者被关闭，返回null
            TextMessage message = (TextMessage) consumer.receive(500);
            if (null != message) {
                messageText = message.getText();
                message.acknowledge();//客户端手动处理消息
            }

//            consumer.close();
//            session.close();

            return messageText;
        } catch (Exception e) {
            log.error("recieveMsg发生异常", e);
            return "";
        } finally {
//            if (connection != null)
//                connection.close();
        }
    }


    /**
     * 需要设置之后才能使用，所以暂时不能使用
     * <p>
     * <broker xmlns="http://activemq.apache.org/schema/core" schedulePeriodForDestinationPurge="30000" brokerName="broker" useJmx="true" dataDirectory="${activemq.data}">
     * schedulePeriodForDestinationPurge这个是设置mq自动清理消息的标签。因为在消息消费完毕之后，topic还会依然存在，个人强迫症。
     * useJmx表示开启jmx监控
     * 再设置：
     * <managementContext>
     * <managementContext createConnector="true" jmxDomainName="myDomain" connectorPath="/jmxrmi" connectorPort="11099"/>
     * </managementContext>
     *
     * @throws IOException
     * @throws MalformedObjectNameException
     */
    public static void getStatus() throws IOException, MalformedObjectNameException {
        JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:2011/jmxrmi");
        JMXConnector connector = JMXConnectorFactory.connect(url, null);
        connector.connect();
        MBeanServerConnection connection = connector.getMBeanServerConnection();

        // 需要注意的是，这里的my-broker必须和上面配置的名称相同
        ObjectName name = new ObjectName("my-broker:BrokerName=localhost,Type=Broker");
        BrokerViewMBean mBean = (BrokerViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection, name, BrokerViewMBean.class, true);
        // System.out.println(mBean.getBrokerName());

        for (ObjectName queueName : mBean.getQueues()) {
            QueueViewMBean queueMBean = (QueueViewMBean) MBeanServerInvocationHandler.newProxyInstance(connection, queueName, QueueViewMBean.class, true);
            System.out.println("\n------------------------------\n");

            // 消息队列名称
            System.out.println("States for queue --- " + queueMBean.getName());

            // 队列中剩余的消息数
            System.out.println("Size --- " + queueMBean.getQueueSize());

            // 消费者数
            System.out.println("Number of consumers --- " + queueMBean.getConsumerCount());

            // 出队数
            System.out.println("Number of dequeue ---" + queueMBean.getDequeueCount());
        }
    }

    /**
     * 关闭连接
     */
    public void close() {
        try {
            if (null != connection) {
                connection.close();
            }
        } catch (Throwable ignore) {
        }
    }

    public static void main(String[] args) throws JMSException {
        //ActiveMQUtils activeMQ = new ActiveMQUtils(new ActiveMQConfig("root", "123456", "tcp://127.0.0.1:61616"));
        ActiveMQUtils activeMQ = new ActiveMQUtils(new ActiveMQConfig("root", "123456", "tcp://127.0.0.1:61616"));

        for (int i = 1; i <= 100; i++) {
            activeMQ.sendMsg("hahaha_queue", "ths is a multithreading");
        }

        Integer pos = 0;
        while (true) {
            pos++;
            String messages = activeMQ.recieveMsg("hahaha_queue");
            System.out.println("Item:" + pos);
            System.out.println(messages);

            if (pos > 200) {
                break;
            }
        }

    }
}