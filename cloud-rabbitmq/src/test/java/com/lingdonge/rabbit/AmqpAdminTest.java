package com.lingdonge.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class AmqpAdminTest {

    @Autowired
    private AmqpAdmin amqpAdmin;

    /**
     * 获取消息数量：https://stackoverflow.com/questions/45764747/spring-boot-rabbitmq-queue-count
     *
     * @throws Exception
     */
    @Test
    public void purgeQueue() throws Exception {
        Integer count = (Integer) amqpAdmin.getQueueProperties("queue_name").get("QUEUE_MESSAGE_COUNT");
        System.out.println(count);
        amqpAdmin.purgeQueue("queue_name", true); // 清空队列
    }

}
