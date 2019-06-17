package com.lingdonge.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class StartApplication {

    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("jdbc.xml");
    JdbcTemplate jdbcTemplate = applicationContext.getBean("jdbcTemplate", JdbcTemplate.class);

}