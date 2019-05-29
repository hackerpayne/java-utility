package com.lingdonge.redis;

import com.lingdonge.redis.annotation.EnableRedisBloomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableRedisBloomFilter
public class DistLockDemoApplication implements CommandLineRunner {
    private final static Logger logger = LoggerFactory.getLogger(DistLockDemoApplication.class);

    public static void main(String[] args) {
        logger.info("<<<<<<<<<<<<<<< DistLockDemoApplication 程序已经启动 >>>>>>>>>>>>>>>>>>");
        SpringApplication.run(DistLockDemoApplication.class, args);

    }

    @Override
    public void run(String... strings) {
        logger.info("启动！");
    }
}