package com.lingdonge.rabbit.consumerThread1;

import com.kyle.utility.threads.ThreadUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 开启一个线程异步批量消费
 */
@Component
@Lazy(value = false)
@Slf4j
public class BatchConsumerDemo {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String QUEUE_NAME = "队列名称";

    private static final ExecutorService executor = Executors.newFixedThreadPool(1);

    private static final int BATCH_SIZE = 100; // 每批处理的数据量大小

    @PostConstruct
    public void init() {
        executor.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                execute();
                return null;
            }
        });
    }

    /**
     * 批量拉取消息，客户端可以连续调用 basicGet 方法拉取多条消息，处理完成之后一次性ACK
     */
    private void executeNew() {
        while (true) {
            rabbitTemplate.execute(new ChannelCallback<Object>() {
                @Override
                public Object doInRabbit(Channel channel) throws Exception {
                    List<GetResponse> responseList = Lists.newArrayList();
                    long tag = 0;
                    while (responseList.size() < BATCH_SIZE) {
                        GetResponse getResponse = channel.basicGet(QUEUE_NAME, false);
                        if (getResponse == null) {
                            break;
                        }
                        responseList.add(getResponse);
                        tag = getResponse.getEnvelope().getDeliveryTag();
                    }
                    if (responseList.isEmpty()) {
                        TimeUnit.MILLISECONDS.sleep(1);
                    } else {
                        log.info("Get <{}> responses this batch", responseList.size());
                        // handle messages 处理收到的消息列表

                        channel.basicAck(tag, true); // 批量确认消费完成
                    }

                    return null;
                }
            });
        }
    }

    private void execute() {
        while (true) {
            rabbitTemplate.execute(new ChannelCallback<String>() {
                @Override
                public String doInRabbit(Channel channel) throws Exception {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        final AMQP.Queue.DeclareOk ok = channel.queueDeclare(QUEUE_NAME, true, false, false, null);
                        int messageCount = ok.getMessageCount();
                        log.debug("accountLogBatchListener {}, msg count {}", sdf.format(new Date()), messageCount);
                        if (messageCount == 0) {
                            return null;
                        }
                        List<String> list = Lists.newArrayList();
                        channel.basicQos(BATCH_SIZE);
                        DefaultConsumer queueingConsumer = new DefaultConsumer(channel);
                        log.debug("channel id {}", Integer.toHexString(System.identityHashCode(channel)));
                        final String inConsumerTag = "accountLogBatchListener {}" + sdf.format(new Date());
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
                        log.info("accountLogBatchListener done {}", sdf.format(new Date()));
                    }
                    channel.abort();
                    return null;
                }
            });

            ThreadUtil.sleep(10 * 1000);// 休息10秒继续处理
        }
    }
}
