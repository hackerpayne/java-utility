package com.lingdonge.rabbit.consumerThread1;

import com.lingdonge.core.threads.ThreadUtil;
import com.lingdonge.rabbit.SpringBaseTest;
import com.lingdonge.rabbit.consume.ChannelCallBackBatch;
import com.lingdonge.rabbit.consume.ChannelCallBackOne;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import javax.annotation.Resource;
import java.util.List;

/**
 * 手动拉取消息测试
 */
@EnableAutoConfiguration
@Slf4j
public class ConsumerPullManaualTest extends SpringBaseTest {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testChannelCallbackOne() {
        log.info("开始单条记录进行消费");
        String queueName = "proxy_pool_all";
        byte[] consumeResult = rabbitTemplate.execute(new ChannelCallBackOne(queueName));
        log.info("消费结果：{}", new String(consumeResult));
    }

    /**
     * 批量拉取消息，客户端可以连续调用 basicGet 方法拉取多条消息，处理完成之后一次性ACK
     */
    @Test
    public void testChannelCallbackBatch() {
        log.info("开始单条记录进行消费");
        String queueName = "proxy_pool_all";

        List<byte[]> consumeResult = null;
        while (true) {
            consumeResult = rabbitTemplate.execute(new ChannelCallBackBatch(queueName, 100));
            if (null == consumeResult) {
                ThreadUtil.sleep(1 * 1000);
                log.info("无结果休息中");
                continue;
            }

            consumeResult.forEach(item -> {
                log.info("消费结果：{}", new String(item));
            });

            log.info("<==================== 一批消息消费完成 ====================>");

        }

    }


}
