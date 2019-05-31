package com.lingdonge.rabbit.service;

import com.alibaba.fastjson.JSON;
import com.lingdonge.core.file.io.IOUtil;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * RabbitMQ操作类
 * 参考官方：http://www.rabbitmq.com/api-guide.html
 */
@Slf4j
public class RabbitMQUtils {

    /**
     * 配置文件
     */
    private static RabbitProperties rabbitProperties;

    /**
     * 接工厂
     */
    private ConnectionFactory connectionFactory;

    /**
     * 连接
     */
    private Connection connection;

    /**
     * 线程缓存池
     */
    private ConcurrentHashMap<String, Channel> mapThreadChannel = new ConcurrentHashMap<>();

    /**
     *
     */
    private ConcurrentHashMap<String, String> mapThreadQueueName = new ConcurrentHashMap<>();

    /**
     * 构造函数，不做操作
     */
    public RabbitMQUtils() {

    }

    /**
     * 构造函数
     *
     * @param config
     */
    public RabbitMQUtils(RabbitProperties config) throws IOException, TimeoutException {
        RabbitMQUtils.rabbitProperties = config;
        this.afterPropertySet();// 注入和生成实例
        log.debug("<<<<<<<<<<<<<<< 正在加载 RabbitUtils 配置文件:{} >>>>>>>>>>>>>>>>>> ：", JSON.toJSONString(config));
    }

    private static volatile RabbitMQUtils instance;

    /**
     * 单例模式，线程安全
     *
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public static RabbitMQUtils getInstance() throws IOException, TimeoutException {
        // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
        if (instance == null) {
            //同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
            synchronized (RabbitMQUtils.class) {
                //未初始化，则初始instance变量
                if (instance == null) {
                    instance = new RabbitMQUtils();
                    instance.afterPropertySet();//初始化配置和Pool池
                }
            }
        }
        return instance;
    }

    /**
     * 实例化完成之后执行
     *
     * @throws IOException
     * @throws TimeoutException
     */
    public void afterPropertySet() throws IOException, TimeoutException {
        log.info("RabbitMQUtils执行afterPropertySet方法");

        // 创建连接工厂
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost()); // 主机
        connectionFactory.setPort(rabbitProperties.getPort()); // 端口
        connectionFactory.setUsername(rabbitProperties.getUsername()); // 用户名
        connectionFactory.setPassword(rabbitProperties.getPassword()); // 密码
        connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost()); // 主机路径
        connectionFactory.setConnectionTimeout(30000);

        // 设置自动恢复，参考https://www.rabbitmq.com/api-guide.html#recovery
        connectionFactory.setAutomaticRecoveryEnabled(true);//设置网络异常重连
        connectionFactory.setNetworkRecoveryInterval(10000);//设置 没10s ，重试一次
//        connectionFactory.setTopologyRecoveryEnabled(false);//设置不重新声明交换器，队列等信息。

//        int threadNumber = 2;
//        final ExecutorService threadPool =  new ThreadPoolExecutor(threadNumber, threadNumber,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>());

        // 关键所在，指定线程池，最好用自己设定的线程池处理
        ExecutorService service = Executors.newFixedThreadPool(20);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.info("Shutting down RabbitMQUtils thread pool...");
                service.shutdown();
                try {
                    while (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                    }

                    if (connection != null) {
                        connection.abort();
                        connection.close();
                    }
                } catch (Exception e) {
                    log.info("Close RabbitMQUtils Error : " + e.getMessage());
                }
                log.info("RabbitMQUtils Thread pool shut down.");
            }
        });

        connectionFactory.setSharedExecutor(service);

        // 创建连接
//        connection = connectionFactory.newConnection();

    }


    /**
     * 创建连接池
     *
     * @return
     */
    public ConnectionFactory getConnectionFactory() throws IOException, TimeoutException {

        if (null == connectionFactory) {
            this.afterPropertySet();
        }
        return connectionFactory;
    }

    /**
     * 创建一个连接
     *
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public Connection getConnection() throws IOException, TimeoutException {

        if (null == connection) {
            connection = connectionFactory.newConnection();
        }
        return connection;
    }


    /**
     * 发送延时消息
     *
     * @param ttlQueue        存放过期时间的队列
     * @param deadLetterQueue 死信转发队列
     * @param msg             消息内容
     * @param seconds         过期时间，单位秒
     */
    public void sendTTLMsg(String ttlQueue, String deadLetterQueue, Object msg, Integer seconds) {
        Channel channel = null;

        try {
            // 创建频道
            channel = getConnection().createChannel();

            // 配置消息头
            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-dead-letter-exchange", "");
            args.put("x-dead-letter-routing-key", deadLetterQueue);

            // 配置收信队列。使用上面的消息头设置，消息过期之后，交给死信队列处理
            channel.queueDeclare(ttlQueue, true, false, false, args);

            // 绑定死信队列
            channel.queueDeclare(deadLetterQueue, true, false, false, null);

            // 发送消息
            channel.basicPublish("", ttlQueue, new AMQP.BasicProperties.Builder().expiration(String.valueOf(seconds)).build(), IOUtil.objToBytes(msg));

        } catch (Exception ex) {
            log.error("recieveMsg发生异常", ex);
        } finally {
            closeChannel(channel);
        }
    }


    /**
     * 往队列里面发送消息
     *
     * @param queueName
     * @param msg
     */
    public void sendMsg(String queueName, String msg) {

        Channel channel = null;
        try {
            // 创建频道
            channel = getConnection().createChannel();

            // 声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
            channel.queueDeclare(queueName, true, false, false, null);

            // 队列发送一条消息
            // 参数1：exchangeName
            channel.basicPublish("", queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());

        } catch (Exception ex) {
            log.error("recieveMsg发生异常", ex);
        } finally {
            closeChannel(channel);
        }
    }

    /**
     * 接收队列里面的消息
     *
     * @param queueName
     * @return
     * @throws IOException
     */
    public String recieveMsg(String queueName) {


        String receiveMsg = "";

        Channel channel = null;
        try {

            channel = getConnection().createChannel();

//            direct直接绑定队列进行消息的消费
            channel.queueDeclare(queueName, true, false, false, null);

            // 每次从队列获取的数量,设置一条条的应答
//        channel.basicQos(0,1,false);
            channel.basicQos(1);

            // 方法一：同步获取一条数据
            // 参数2为：autoAck，如果设置autoAck为false，那么你同样需要显示的调用Channel.basicAck来确认消息已经被成功的接受了：
            boolean autoAck = true;
            GetResponse getResponse = channel.basicGet(queueName, autoAck);
            if (getResponse != null) {
                receiveMsg = new String(getResponse.getBody(), "UTF-8");
            }

            // 方法二：异步获取多条数据
//        final ArrayBlockingQueue<String> replyHandoff = new ArrayBlockingQueue<String>(
//                1);


//
//        final Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//
//                String message = new String(body, "UTF-8");
//
//                replyHandoff.add(message);
//
//                channel.basicAck(envelope.getDeliveryTag(), false);//确认消息已收到
//
//            }
//        };

//        boolean autoAck = false;
//
//        //消息消费完成确认
//        channel.basicConsume(queueName, autoAck, consumer);
            return receiveMsg;
        } catch (Exception ex) {
            log.error("recieveMsg发生异常", ex);
            return receiveMsg;
        } finally {
            closeChannel(channel);
        }
    }

    /**
     * 发送消息
     *
     * @param exchangeName
     * @param msg
     */
    public void sendFanoutMsg(String exchangeName, String msg) {
        Channel channel = null;
        try {
            channel = mapThreadChannel.get(exchangeName + "_" + Thread.currentThread().getId());
            if (channel == null) {
                channel = getConnection().createChannel();
                channel.exchangeDeclare(exchangeName, "fanout");
                mapThreadChannel.putIfAbsent(exchangeName + "_" + Thread.currentThread().getId(), channel);
            }
            channel.basicPublish(exchangeName, "", null, msg.getBytes());
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        } finally {
            closeChannel(channel);
        }
    }

    /**
     * 接收Fanout消息，每个不同的启动线程都会收到此消息
     *
     * @param exchangeName
     * @return
     */
    public String recieveFanoutMsg(String exchangeName) {

        String receiveMsg = "";
        Channel channel = null;
        try {
            channel = mapThreadChannel.get(exchangeName + "_" + Thread.currentThread().getId());
            String fanoutQueueName = mapThreadQueueName.get(exchangeName + "_queueName_" + Thread.currentThread().getId());

            // 从缓存中读取Channel使用，保证同线程不重复
            if (channel == null || StringUtils.isEmpty(fanoutQueueName)) {
                channel = getConnection().createChannel();

                // 声明消息路由的名称和类型
                channel.exchangeDeclare(exchangeName, "fanout");

                // 生成随机消息队列
                fanoutQueueName = channel.queueDeclare().getQueue();//获取自动生成的QueueName

//            System.out.println("获取到的QueueName为：" + queueName);

                mapThreadChannel.putIfAbsent(exchangeName + "_" + Thread.currentThread().getId(), channel);
                mapThreadQueueName.put(exchangeName + "_queueName_" + Thread.currentThread().getId(), fanoutQueueName);

                // 绑定消息队列和路由
                channel.queueBind(fanoutQueueName, exchangeName, "");// 绑定ExChange，之后Exchange的消息就会进入这个自动的消息
            }

            channel.basicQos(1);

            // 方法一：同步获取一条数据
            // 参数2为：autoAck，如果设置autoAck为false，那么你同样需要显示的调用Channel.basicAck来确认消息已经被成功的接受了：
            boolean autoAck = true;
            GetResponse getResponse = channel.basicGet(fanoutQueueName, autoAck);
            if (getResponse != null) {
                receiveMsg = new String(getResponse.getBody(), "UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        } finally {
            closeChannel(channel);
        }

        return receiveMsg;

    }

    /**
     * 消息发送到topic类型的exchange上时不能随意指定routing_key（一定是指由一系列由点号连接单词的字符串，单词可以是任意的，但一般都会与消息或多或少的有些关联）。Routing key的长度不能超过255个字节。
     * <p>
     * （星号）：可以（只能）匹配一个单词
     * #（井号）：可以匹配多个单词（或者零个）
     *
     * @param topicName
     * @param msg
     */
    public void sendTopicMsg(String topicName, String msg) {

        Channel channel = null;
        try {
            // 创建频道
            channel = getConnection().createChannel();

            // 指定一个topic类型的exchange
            channel.exchangeDeclare(topicName, "topic");

            // 队列发送一条消息
            // 参数1：exchangeName
            channel.basicPublish(topicName, "", MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());

        } catch (Exception ex) {
            log.error("recieveMsg发生异常", ex);
        } finally {
            closeChannel(channel);
        }
    }


    /**
     * 接收队列里面的消息
     *
     * @param exchangeName
     * @return
     * @throws IOException
     */
    public String recieveTopicMsg(String exchangeName, String oneMatchTopic) {


        String receiveMsg = "";

        Channel channel = null;
        try {

            channel = getConnection().createChannel();

            // 指定一个Topic类型的Exchange
            channel.exchangeDeclare(exchangeName, "topic");


            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, exchangeName, oneMatchTopic);

//            for (String bindingKey : listTopics) {
//                channel.queueBind(queueName, exchangeName, bindingKey);
//            }

            // 每次从队列获取的数量,设置一条条的应答
//        channel.basicQos(0,1,false);
            channel.basicQos(1);

            // 方法一：同步获取一条数据
            // 参数2为：autoAck，如果设置autoAck为false，那么你同样需要显示的调用Channel.basicAck来确认消息已经被成功的接受了：
            boolean autoAck = true;
            GetResponse getResponse = channel.basicGet(queueName, autoAck);
            if (getResponse != null) {
                receiveMsg = new String(getResponse.getBody(), "UTF-8");
            }

            // 方法二：异步获取多条数据
//        final ArrayBlockingQueue<String> replyHandoff = new ArrayBlockingQueue<String>(
//                1);


//
//        final Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
//
//                String message = new String(body, "UTF-8");
//
//                replyHandoff.add(message);
//
//                channel.basicAck(envelope.getDeliveryTag(), false);//确认消息已收到
//
//            }
//        };

//        boolean autoAck = false;
//
//        //消息消费完成确认
//        channel.basicConsume(queueName, autoAck, consumer);
            return receiveMsg;
        } catch (Exception ex) {
            log.error("recieveMsg发生异常", ex);
            return receiveMsg;
        } finally {
            closeChannel(channel);
        }


    }

    /**
     * 关闭Channel
     *
     * @param channel Channel标记;
     */
    public static void closeChannel(Channel channel) {
        try {
//                if (channel != null) channel.abort();

            if (channel != null) {
                channel.close();
            }

        } catch (Exception e) {
            log.error("closeChannel发生异常", e);
        }
    }

    /**
     * 关闭连接connection
     *
     * @param connection
     */
    public static void closeConnection(Connection connection) {
        try {
//                if (channel != null) channel.abort();

            if (connection != null) {
                connection.close();
            }

        } catch (Exception e) {
            log.error("closeConnection发生异常", e);
        }
    }
}
