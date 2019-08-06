package com.lingdonge.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import javax.annotation.Resource;
import java.util.HashMap;

@EnableAutoConfiguration // 自动加载配置文件
@Slf4j
public class AmqpAdminTest extends SpringBaseTest {

    @Resource
    private RabbitAdmin rabbitAdmin;

    @Resource
    private RabbitProperties rabbitProperties;

    @Test
    public void testProperties() {
        System.out.println(rabbitProperties);
    }

    /**
     * 获取消息数量：https://stackoverflow.com/questions/45764747/spring-boot-rabbitmq-queue-count
     *
     * @throws Exception
     */
    @Test
    public void getQueueMessageCount() throws Exception {
        String queue = "queue_ebdoor.com_low";
        Integer count = (Integer) rabbitAdmin.getQueueProperties(queue).get(RabbitAdmin.QUEUE_MESSAGE_COUNT);
        System.out.println(count);
    }

    @Test
    public void testAdmin() throws Exception {
        rabbitAdmin.declareExchange(new DirectExchange("test.direct", false, false));
        rabbitAdmin.declareExchange(new TopicExchange("test.topic", false, false));
        rabbitAdmin.declareExchange(new FanoutExchange("test.fanout", false, false));
        rabbitAdmin.declareQueue(new Queue("test.direct.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.topic.queue", false));
        rabbitAdmin.declareQueue(new Queue("test.fanout.queue", false));

        rabbitAdmin.declareBinding(new Binding("test.direct.queue", Binding.DestinationType.QUEUE, "test.direct", "direct", new HashMap<>()));

        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.topic.queue", false))        //直接创建队列
                        .to(new TopicExchange("test.topic", false, false))    //直接创建交换机 建立关联关系
                        .with("user.#"));    //指定路由Key

        rabbitAdmin.declareBinding(
                BindingBuilder
                        .bind(new Queue("test.fanout.queue", false))
                        .to(new FanoutExchange("test.fanout", false, false)));

        //清空队列数据
        rabbitAdmin.purgeQueue("test.topic.queue", false);
    }
}
