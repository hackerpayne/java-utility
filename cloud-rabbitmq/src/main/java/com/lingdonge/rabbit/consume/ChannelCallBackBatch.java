package com.lingdonge.rabbit.consume;

import com.google.common.collect.Lists;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.ChannelCallback;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 批量拉取数据回本地
 */
@Slf4j
public class ChannelCallBackBatch implements ChannelCallback<List<byte[]>> {

    private String queueName;

    private Integer batchSize;

    private Long sleepTime;

    /**
     * 100条1批消费数据
     *
     * @param queueName
     */
    public ChannelCallBackBatch(String queueName) {
        this(queueName, 100, 1L);
    }

    /**
     * @param queueName
     * @param batchSize
     */
    public ChannelCallBackBatch(String queueName, Integer batchSize) {
        this(queueName, batchSize, 1L);
    }

    /**
     * 批量拉取数量
     *
     * @param queueName 拉取队列名
     * @param batchSize 每批次拉取的数据量
     * @param sleepTime 队列不够数据时，休眠时长，单位秒
     */
    public ChannelCallBackBatch(String queueName, Integer batchSize, Long sleepTime) {
        this.queueName = queueName;
        this.batchSize = batchSize;
        this.sleepTime = sleepTime;
    }

    @Override
    public List<byte[]> doInRabbit(Channel channel) throws Exception {
        List<byte[]> responseList = Lists.newArrayList();
        long tag = 0;
        while (responseList.size() < batchSize) {
            GetResponse getResponse = channel.basicGet(queueName, false);
            if (getResponse == null) {
                TimeUnit.MILLISECONDS.sleep(sleepTime);
                continue;
            }
            responseList.add(getResponse.getBody());
            tag = getResponse.getEnvelope().getDeliveryTag();
        }
        if (responseList.isEmpty()) {
            TimeUnit.MILLISECONDS.sleep(sleepTime);
        } else {
            log.info("ChannelCallBackBatch Get <{}> responses this batch", responseList.size());
            // handle messages 处理收到的消息列表，或者打包返回

            channel.basicAck(tag, true); // 批量确认消费完成
            return responseList;
        }

        return null;
    }
}
