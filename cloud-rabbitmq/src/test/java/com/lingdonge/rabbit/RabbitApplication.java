package com.lingdonge.rabbit;

import com.lingdonge.rabbit.annotation.EnableKyleRabbitMQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 *
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableKyleRabbitMQ
@Slf4j
public class RabbitApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(RabbitApplication.class, args);
        log.info("Start RabbitApplication class exe");
    }

    @Override
    public void run(String... strings) {
    }

}
