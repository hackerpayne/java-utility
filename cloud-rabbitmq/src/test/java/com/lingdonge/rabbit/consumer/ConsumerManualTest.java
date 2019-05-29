package com.lingdonge.rabbit.consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class ConsumerManualTest {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Test
    public void Receiver() throws Exception {
        Message message = rabbitTemplate.receive("topic.message");
        if (null == message) {
            System.out.println("无数据消费~！");
        } else {
            String result = message.getBody() != null ? new String(message.getBody()) : "";
            System.out.println("消费第【" + message.getMessageProperties().getMessageCount() + "】条数据，消费记录：" + result);
        }
    }


}
