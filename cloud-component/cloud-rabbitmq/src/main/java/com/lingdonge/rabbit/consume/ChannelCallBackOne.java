package com.lingdonge.rabbit.consume;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import org.springframework.amqp.rabbit.core.ChannelCallback;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 单条消息拉取，结果转为 byte[]
 */
public class ChannelCallBackOne implements ChannelCallback<byte[]> {

    private String queueName;

    public ChannelCallBackOne(String queueName) {
        this.queueName = queueName;
    }

    @Override
    public byte[] doInRabbit(Channel channel) {
        GetResponse response = null;
        try {
            response = channel.basicGet(queueName, false); // 参数2，是否自动确认，这里否，使用手动确认进行拉取数据
            if (null == response) {
                TimeUnit.MICROSECONDS.sleep(1);
                return null;
            }
//            String message = new String(response.getBody());
            channel.basicAck(response.getEnvelope().getDeliveryTag(), false); // 手动设置消息已经被消费处理
            return response.getBody();
        } catch (Exception e) {
            try {
                channel.basicNack(response.getEnvelope().getDeliveryTag(), false, true); // 设置消息未被消费
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}