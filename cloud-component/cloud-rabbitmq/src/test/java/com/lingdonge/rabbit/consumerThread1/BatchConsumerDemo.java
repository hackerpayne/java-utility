package com.lingdonge.rabbit.consumerThread1;

import com.lingdonge.core.dates.LocalDateUtil;
import com.lingdonge.core.threads.ThreadUtil;
import com.lingdonge.rabbit.SpringBaseTest;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 开启一个线程异步批量消费
 */
@EnableAutoConfiguration // 自动加载配置文件
@Slf4j
public class BatchConsumerDemo extends SpringBaseTest {

    private static final ExecutorService executor = Executors.newFixedThreadPool(1);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String QUEUE_NAME = "sendqueueKey";

    private static final int BATCH_SIZE = 100; // 每批处理的数据量大小


    @Test
    public void batchConsumerNew() {
        while (true) {
            rabbitTemplate.execute(new ChannelCallback<String>() {
                @Override
                public String doInRabbit(Channel channel) throws Exception {
                    try {
                        final AMQP.Queue.DeclareOk ok = channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                        int messageCount = ok.getMessageCount();
                        log.info("当前：【{}】，队列【{}】，消息数量为【{}】", LocalDateUtil.getNowTime(), QUEUE_NAME, messageCount);
                        if (messageCount == 0) {
                            return null;
                        }

                        List<String> list = Lists.newArrayList();
                        channel.basicQos(BATCH_SIZE); // 批量拉取
                        DefaultConsumer queueingConsumer = new DefaultConsumer(channel);
                        log.debug("channel id {}", Integer.toHexString(System.identityHashCode(channel)));
                        final String inConsumerTag = "accountLogBatchListener {}" + LocalDateUtil.getNowTime();
                        channel.basicConsume(QUEUE_NAME, false, inConsumerTag, queueingConsumer);
                        long messageId = -1;
                        int dealedCount = 0;
                        int i = BATCH_SIZE;
                        while (i-- > 0) {
//                            Delivery delivery = queueingConsumer.handleDelivery(BATCH_SIZE);
//                            if (delivery == null) {
//                                break;
//                            }
//                            String msg = new String(delivery.getBody());
//                            String logStr = JSONObject.parseObject(msg, String.class);
//                            list.add(logStr);
//                            messageId = delivery.getEnvelope().getDeliveryTag();
//                            log.info(" userId {}, delivery id {}", logStr, messageId);
//
//                            dealedCount++;
//                            if (dealedCount % 5 == 0) {
//                                channel.basicAck(messageId, true);
//                                log.debug("batch ack message id =>{}", messageId);
//                                messageId = -1;
//                            }
                        }
                        if (messageId > 0) {
                            channel.basicAck(messageId, true);
                            log.debug("last to ack message id =>{}", messageId);
                        }

                        // 批量入库
//                        accountLogService.saveBatch(list);


                    } finally {
                        log.info("accountLogBatchListener done {}", LocalDateUtil.getNowTime());
                    }
                    channel.abort();
                    return null;
                }
            });

            ThreadUtil.sleep(10 * 1000);// 休息10秒继续处理
        }
    }
}
