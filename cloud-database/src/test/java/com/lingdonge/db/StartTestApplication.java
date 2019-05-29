package com.lingdonge.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class StartTestApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(StartTestApplication.class);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
