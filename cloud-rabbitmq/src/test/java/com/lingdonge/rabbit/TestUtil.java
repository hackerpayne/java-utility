package com.lingdonge.rabbit;

import com.lingdonge.rabbit.service.RabbitMQUtils;
import com.rabbitmq.client.Connection;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestUtil {


    private static RabbitMQUtils getRabbitMQUtils()  {
        RabbitProperties rabbitProperties = new RabbitProperties();
        rabbitProperties.setHost("localhost");
        rabbitProperties.setPort(5672);
        rabbitProperties.setUsername("kyle250");
        rabbitProperties.setPassword("123455");
        rabbitProperties.setVirtualHost("/");

        try {
            return new RabbitMQUtils(rabbitProperties);
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getRabbitConn(){
        try {
            return getRabbitMQUtils().getConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return null;
    }

}
