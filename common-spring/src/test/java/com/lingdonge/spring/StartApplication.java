package com.lingdonge.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class StartApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartApplication.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info(">>>>>>>>>>>>>>> 服务启动执行，执行加载数据等操作 <<<<<<<<<<<<<");
    }

    public static void main(String[] args) {
        SpringApplication.run(StartApplication.class, args);
    }
}